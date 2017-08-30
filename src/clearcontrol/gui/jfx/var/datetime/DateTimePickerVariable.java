package clearcontrol.gui.jfx.var.datetime;

import java.time.LocalDateTime;
import javafx.application.Platform;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.datetime.DateTimePicker;

/**
 * Date and time picker that syncs with a variable
 *
 * @author royer
 */
public class DateTimePickerVariable extends DateTimePicker
{

  private Variable<LocalDateTime> mLocalDateTimeVariable;

  /**
   * Instanciates a date and time picker that syncs with a given variable
   * 
   * @param pLocalDateTimeVariable
   *          variabel to sync with
   * 
   */
  public DateTimePickerVariable(Variable<LocalDateTime> pLocalDateTimeVariable)
  {
    super();
    mLocalDateTimeVariable = pLocalDateTimeVariable;

    if (mLocalDateTimeVariable.get() != null)
      setDateTimeValue(mLocalDateTimeVariable.get());

    mLocalDateTimeVariable.addSetListener((o, n) -> {
      if (!n.equals(o) && n != null)
        Platform.runLater(() -> {
          if (n.equals(getDateTimeValue()))
            return;
          setDateTimeValue(n);
        });
    });

    showingProperty().addListener((obs, o, n) -> {
      if (!n)
        if (!mLocalDateTimeVariable.get().equals(getDateTimeValue()))
          mLocalDateTimeVariable.setAsync(getDateTimeValue());
    });

    /*setOnMousePressed((e) -> {
      mLocalDateTimeVariable.setAsync(getDateTimeValue());
    });
    
    /*getDateTimeValueProperty().addListener((obs, o, n) -> {
      if (n != o)
        mLocalDateTimeVariable.setAsync(getDateTimeValue());
    });/**/

  }

}
