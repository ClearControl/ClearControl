package rtlib.gui.halcyon;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContextMenuBuilder;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.list.HalcyonNodeRepository;
import model.list.HalcyonNodeRepositoryListener;
import model.node.HalcyonNode;
import model.node.HalcyonNodeInterface;
import view.ViewManager;
import window.control.ControlWindowBase;

/**
 * Device Config Window
 */
public class ConfigWindow extends ControlWindowBase
{
	private Properties mProperties;

	final private HashMap<String, TreeItem<TreeNode>> subNodes = new HashMap<>();
	final TreeView<TreeNode> tree;
	JFXPanel fxPanel;
	ContextMenu rootContextMenu;


	public ConfigWindow()
	{
		super( new VBox());


		try
		{
			mProperties = new Properties();
			InputStream lResourceAsStream = ConfigWindow.class.getResourceAsStream("./icons/IconMap.properties");
			mProperties.load(lResourceAsStream);
			lResourceAsStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		setTitle( "Config" );

		String lRootIconFileName = mProperties.getProperty("root.icon");
		Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("./icons/" + lRootIconFileName)));

		TreeItem<TreeNode> rootItem = new TreeItem<>( new TreeNode( "Microscopy" ), rootIcon );
		rootItem.setExpanded( true );

		for (NodeType type : NodeType.values())
		{
			String iconName = mProperties.getProperty(type.name()
																											.toLowerCase() + ".icon");
			String lRessourceName = "./icons/" + iconName;
			Class<? extends ConfigWindow> lClassToFindRessources = getClass();
			InputStream lIconResourceAsStream = lClassToFindRessources.getResourceAsStream(lRessourceName);

			Node icon = new ImageView(new Image(lIconResourceAsStream));

			TreeItem<TreeNode> node = new TreeItem<>( new TreeNode( type.name() ), icon );
			node.setExpanded( true );
			subNodes.put( type.name(), node);
			rootItem.getChildren().add( node );
		}

		tree = new TreeView<>( rootItem );
		tree.getSelectionModel().setSelectionMode( SelectionMode.MULTIPLE );

		setContents( tree );
	}

	@Override
	public void setNodes( HalcyonNodeRepository nodes )
	{
		this.nodes = nodes;

		this.nodes.addListener( new HalcyonNodeRepositoryListener()
		{
			@Override public void nodeAdded( HalcyonNodeInterface node )
			{
				TreeItem<TreeNode> item = new TreeItem<>( new TreeNode( node.getName(), node ) );
				subNodes.get(node.getType().name()).getChildren().add( item );
			}

			@Override public void nodeRemoved( HalcyonNodeInterface node )
			{
				subNodes.get(node.getType().name()).getChildren().remove( node.getName() );
			}
		} );
	}

	@Override
	public void setViewManager( ViewManager manager )
	{
		tree.setOnMouseClicked( event -> {
			if (event.getClickCount() == 2)
			{
				TreeItem<TreeNode> item = tree.getSelectionModel().getSelectedItem();
				if(item != null)
				{
					TreeNode node = item.getValue();
					if (node.getNode() != null)
					{
						manager.open( node.getNode() );
					}
				}
			}
		} );

		rootContextMenu = ContextMenuBuilder.create()
				.items(
						MenuItemBuilder.create()
								.text( "Create a Panel" )
								.onAction(
										new EventHandler< ActionEvent >()
										{
											@Override
											public void handle( ActionEvent arg0 )
											{
												ObservableList< TreeItem< TreeNode > > list = tree.getSelectionModel().getSelectedItems();
												VBox vBox = new VBox();

												for ( TreeItem< TreeNode > n : list )
												{
													System.out.println( n.getValue().getName() + " is selected.");
													vBox.getChildren().add(n.getValue().getNode().getPanel());
												}

												HalcyonNode node = new HalcyonNode( "User panel", NodeType.Laser, vBox );
												manager.open( node );
											}
										}
								)
								.build()
				)
				.build();

		tree.setContextMenu( rootContextMenu );
	}

	public void start( TreeView<TreeNode> tree )
	{
		StackPane root = new StackPane();
		root.getChildren().add( tree );

		fxPanel.setScene( new Scene( root, 300, 250 ) );
	}

	private class TreeNode
	{
		private String name;

		private HalcyonNodeInterface node;

		public TreeNode( String name )
		{
			this.name = name;
		}

		public TreeNode( String name, HalcyonNodeInterface node )
		{
			this.name = name;
			this.node = node;
		}

		public String getName()
		{
			return name;
		}

		public void setName( String name )
		{
			this.name = name;
		}

		public HalcyonNodeInterface getNode()
		{
			return node;
		}

		public void setNode( HalcyonNode node )
		{
			this.node = node;
		}

		@Override
		public String toString()
		{
			return this.name;
		}
	}
}
