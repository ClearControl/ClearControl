<?xml version='1.0' encoding='UTF-8'?>
<Project Type="Project" LVVersion="12008004">
	<Item Name="My Computer" Type="My Computer">
		<Property Name="IOScan.Faults" Type="Str"></Property>
		<Property Name="IOScan.NetVarPeriod" Type="UInt">100</Property>
		<Property Name="IOScan.NetWatchdogEnabled" Type="Bool">false</Property>
		<Property Name="IOScan.Period" Type="UInt">10000</Property>
		<Property Name="IOScan.PowerupMode" Type="UInt">0</Property>
		<Property Name="IOScan.Priority" Type="UInt">9</Property>
		<Property Name="IOScan.ReportModeConflict" Type="Bool">true</Property>
		<Property Name="IOScan.StartEngineOnDeploy" Type="Bool">false</Property>
		<Property Name="server.app.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.control.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="server.tcp.enabled" Type="Bool">false</Property>
		<Property Name="server.tcp.port" Type="Int">0</Property>
		<Property Name="server.tcp.serviceName" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.tcp.serviceName.default" Type="Str">My Computer/VI Server</Property>
		<Property Name="server.vi.callsEnabled" Type="Bool">true</Property>
		<Property Name="server.vi.propertiesEnabled" Type="Bool">true</Property>
		<Property Name="specify.custom.address" Type="Bool">false</Property>
		<Item Name="Lib" Type="Folder">
			<Property Name="NI.SortType" Type="Int">3</Property>
			<Item Name="DirettoreOpen.vi" Type="VI" URL="../Lib/DirettoreOpen.vi"/>
			<Item Name="DirettoreStart.vi" Type="VI" URL="../Lib/DirettoreStart.vi"/>
			<Item Name="DirettorePlay.vi" Type="VI" URL="../Lib/DirettorePlay.vi"/>
			<Item Name="DirettoreStop.vi" Type="VI" URL="../Lib/DirettoreStop.vi"/>
			<Item Name="DirettoreClose.vi" Type="VI" URL="../Lib/DirettoreClose.vi"/>
		</Item>
		<Item Name="Tests" Type="Folder" URL="../Tests">
			<Property Name="NI.DISK" Type="Bool">true</Property>
		</Item>
		<Item Name="Direttore.ico" Type="Document" URL="../Icon/Direttore.ico"/>
		<Item Name="Direttore.vi" Type="VI" URL="../Direttore.vi"/>
		<Item Name="FPGA Target" Type="FPGA Target">
			<Property Name="AutoRun" Type="Bool">false</Property>
			<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
			<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			<Property Name="Mode" Type="Int">0</Property>
			<Property Name="NI.LV.FPGA.CLIPDeclarationsArraySize" Type="Int">0</Property>
			<Property Name="NI.LV.FPGA.CLIPDeclarationSet" Type="Xml">
<CLIPDeclarationSet>
</CLIPDeclarationSet></Property>
			<Property Name="NI.LV.FPGA.CompileConfigString" Type="Str">PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
			<Property Name="NI.LV.FPGA.Version" Type="Int">5</Property>
			<Property Name="NI.SortType" Type="Int">3</Property>
			<Property Name="Resource Name" Type="Str">RIO0</Property>
			<Property Name="SWEmulationSubMode" Type="UInt">0</Property>
			<Property Name="SWEmulationVIPath" Type="Path"></Property>
			<Property Name="Target Class" Type="Str">PCIe-7852R</Property>
			<Property Name="Top-Level Timing Source" Type="Str">40 MHz Onboard Clock</Property>
			<Property Name="Top-Level Timing Source Is Default" Type="Bool">true</Property>
			<Item Name="Connector0" Type="Folder">
				<Item Name="Connector0/AI0" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI0</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}</Property>
				</Item>
				<Item Name="Connector0/AI1" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI1</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{87646D82-6E41-4317-BE09-D56D3605B482}</Property>
				</Item>
				<Item Name="Connector0/AI2" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI2</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}</Property>
				</Item>
				<Item Name="Connector0/AI3" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI3</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{1177BCDD-28E4-47F3-99D6-39F4F5F76854}</Property>
				</Item>
				<Item Name="Connector0/AI4" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI4</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{B055064E-A2BE-4C19-ABEA-78F4696980C9}</Property>
				</Item>
				<Item Name="Connector0/AI5" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI5</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}</Property>
				</Item>
				<Item Name="Connector0/AI6" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI6</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}</Property>
				</Item>
				<Item Name="Connector0/AI7" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AI7</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{28083CB5-4426-4CF0-A701-48DEC1CF8810}</Property>
				</Item>
				<Item Name="Connector0/AO0" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO0</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}</Property>
				</Item>
				<Item Name="Connector0/AO1" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO1</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{4186D02A-1DD5-4D55-BC93-B73158B23419}</Property>
				</Item>
				<Item Name="Connector0/AO2" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO2</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{8697EC68-BE40-47CB-BBFE-8C93248A3D85}</Property>
				</Item>
				<Item Name="Connector0/AO3" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO3</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}</Property>
				</Item>
				<Item Name="Connector0/AO4" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO4</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}</Property>
				</Item>
				<Item Name="Connector0/AO5" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO5</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{A8962FFB-6132-40E5-A46F-48FFC78B12D1}</Property>
				</Item>
				<Item Name="Connector0/AO6" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO6</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{1BBFC43A-704E-482E-A473-8A6DC121F385}</Property>
				</Item>
				<Item Name="Connector0/AO7" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="Arbitration">
   <Value>AlwaysArbitrate</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/AO7</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}</Property>
				</Item>
				<Item Name="Connector0/DIO0" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO0</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}</Property>
				</Item>
				<Item Name="Connector0/DIO1" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO1</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}</Property>
				</Item>
				<Item Name="Connector0/DIO2" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO2</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}</Property>
				</Item>
				<Item Name="Connector0/DIO3" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO3</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{AFD10E7B-0219-43A4-A538-71371F66E384}</Property>
				</Item>
				<Item Name="Connector0/DIO4" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO4</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}</Property>
				</Item>
				<Item Name="Connector0/DIO5" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO5</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}</Property>
				</Item>
				<Item Name="Connector0/DIO6" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO6</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}</Property>
				</Item>
				<Item Name="Connector0/DIO7" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO7</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{805477AD-0E4B-45DD-A2F6-63474B371F9E}</Property>
				</Item>
				<Item Name="Connector0/DIO8" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO8</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}</Property>
				</Item>
				<Item Name="Connector0/DIO9" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO9</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}</Property>
				</Item>
				<Item Name="Connector0/DIO10" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO10</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{4BDDF482-2523-490F-9F84-1547DAC8EA91}</Property>
				</Item>
				<Item Name="Connector0/DIO11" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO11</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{BAEE6565-AE5D-4482-B882-9A3136CD7427}</Property>
				</Item>
				<Item Name="Connector0/DIO12" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO12</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{60DA92AE-E34A-4274-A3C8-151C3133D2C9}</Property>
				</Item>
				<Item Name="Connector0/DIO13" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO13</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}</Property>
				</Item>
				<Item Name="Connector0/DIO14" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO14</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}</Property>
				</Item>
				<Item Name="Connector0/DIO15" Type="Elemental IO">
					<Property Name="eioAttrBag" Type="Xml"><AttributeSet name="">
   <Attribute name="ArbitrationForOutputData">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="ArbitrationForOutputEnable">
   <Value>NeverArbitrate</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputData">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForOutputEnable">
   <Value>1</Value>
   </Attribute>
   <Attribute name="NumberOfSyncRegistersForReadInProject">
   <Value>Auto</Value>
   </Attribute>
   <Attribute name="resource">
   <Value>/Connector0/DIO15</Value>
   </Attribute>
</AttributeSet>
</Property>
					<Property Name="FPGA.PersistentID" Type="Str">{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}</Property>
				</Item>
			</Item>
			<Item Name="40 MHz Onboard Clock" Type="FPGA Base Clock">
				<Property Name="FPGA.PersistentID" Type="Str">{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig" Type="Str">ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.Accuracy" Type="Dbl">100</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.ClockSignalName" Type="Str">Clk40</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.MaxDutyCycle" Type="Dbl">50</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.MaxFrequency" Type="Dbl">40000000</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.MinDutyCycle" Type="Dbl">50</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.MinFrequency" Type="Dbl">40000000</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.NominalFrequency" Type="Dbl">40000000</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.PeakPeriodJitter" Type="Dbl">250</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.ResourceName" Type="Str">40 MHz Onboard Clock</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.SupportAndRequireRuntimeEnableDisable" Type="Bool">false</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.TopSignalConnect" Type="Str">Clk40</Property>
				<Property Name="NI.LV.FPGA.BaseTSConfig.VariableFrequency" Type="Bool">false</Property>
				<Property Name="NI.LV.FPGA.Valid" Type="Bool">true</Property>
				<Property Name="NI.LV.FPGA.Version" Type="Int">5</Property>
			</Item>
			<Item Name="Direttore.fpga.vi" Type="VI" URL="../FPGA/Direttore.fpga.vi">
				<Property Name="BuildSpec" Type="Str">{7514B21D-8E0F-40E5-A26B-6317D1BC9650}</Property>
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
				<Property Name="NI.LV.FPGA.InterfaceBitfile" Type="Str">C:\Users\myerslab\workspace\Direttore\labview\direttore\FPGA Bitfiles\Direttore.lvbitx</Property>
			</Item>
			<Item Name="OutputMatrixFIFO" Type="FPGA FIFO">
				<Property Name="Actual Number of Elements" Type="UInt">8197</Property>
				<Property Name="Arbitration for Read" Type="UInt">1</Property>
				<Property Name="Arbitration for Write" Type="UInt">1</Property>
				<Property Name="Control Logic" Type="UInt">0</Property>
				<Property Name="Data Type" Type="UInt">2</Property>
				<Property Name="Disable on Overflow/Underflow" Type="Bool">false</Property>
				<Property Name="fifo.configuration" Type="Str">"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
				<Property Name="fifo.configured" Type="Bool">true</Property>
				<Property Name="fifo.projectItemValid" Type="Bool">true</Property>
				<Property Name="fifo.valid" Type="Bool">true</Property>
				<Property Name="fifo.version" Type="Int">12</Property>
				<Property Name="FPGA.PersistentID" Type="Str">{D924E5B9-0AE1-4803-97A5-42795034A572}</Property>
				<Property Name="Local" Type="Bool">false</Property>
				<Property Name="Memory Type" Type="UInt">2</Property>
				<Property Name="Number Of Elements Per Read" Type="UInt">1</Property>
				<Property Name="Number Of Elements Per Write" Type="UInt">1</Property>
				<Property Name="Requested Number of Elements" Type="UInt">8191</Property>
				<Property Name="Type" Type="UInt">1</Property>
				<Property Name="Type Descriptor" Type="Str">100080000000000100094002000349313600010000000000000000</Property>
			</Item>
			<Item Name="TriggerPlayFIFO" Type="FPGA FIFO">
				<Property Name="Actual Number of Elements" Type="UInt">37</Property>
				<Property Name="Arbitration for Read" Type="UInt">1</Property>
				<Property Name="Arbitration for Write" Type="UInt">1</Property>
				<Property Name="Control Logic" Type="UInt">0</Property>
				<Property Name="Data Type" Type="UInt">3</Property>
				<Property Name="Disable on Overflow/Underflow" Type="Bool">false</Property>
				<Property Name="fifo.configuration" Type="Str">"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
				<Property Name="fifo.configured" Type="Bool">true</Property>
				<Property Name="fifo.projectItemValid" Type="Bool">true</Property>
				<Property Name="fifo.valid" Type="Bool">true</Property>
				<Property Name="fifo.version" Type="Int">12</Property>
				<Property Name="FPGA.PersistentID" Type="Str">{55FC8392-A65A-4E41-83FA-BD831CB8D120}</Property>
				<Property Name="Local" Type="Bool">false</Property>
				<Property Name="Memory Type" Type="UInt">2</Property>
				<Property Name="Number Of Elements Per Read" Type="UInt">1</Property>
				<Property Name="Number Of Elements Per Write" Type="UInt">1</Property>
				<Property Name="Requested Number of Elements" Type="UInt">37</Property>
				<Property Name="Type" Type="UInt">1</Property>
				<Property Name="Type Descriptor" Type="Str">1000800000000001000940030003493332000100000000000000000000</Property>
			</Item>
			<Item Name="InputMatrixFIFO" Type="FPGA FIFO">
				<Property Name="Actual Number of Elements" Type="UInt">8191</Property>
				<Property Name="Arbitration for Read" Type="UInt">1</Property>
				<Property Name="Arbitration for Write" Type="UInt">1</Property>
				<Property Name="Control Logic" Type="UInt">0</Property>
				<Property Name="Data Type" Type="UInt">2</Property>
				<Property Name="Disable on Overflow/Underflow" Type="Bool">false</Property>
				<Property Name="fifo.configuration" Type="Str">"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
				<Property Name="fifo.configured" Type="Bool">true</Property>
				<Property Name="fifo.projectItemValid" Type="Bool">true</Property>
				<Property Name="fifo.valid" Type="Bool">true</Property>
				<Property Name="fifo.version" Type="Int">12</Property>
				<Property Name="FPGA.PersistentID" Type="Str">{922C68B7-7F77-4C05-A15F-EE96A85F9822}</Property>
				<Property Name="Local" Type="Bool">false</Property>
				<Property Name="Memory Type" Type="UInt">2</Property>
				<Property Name="Number Of Elements Per Read" Type="UInt">1</Property>
				<Property Name="Number Of Elements Per Write" Type="UInt">1</Property>
				<Property Name="Requested Number of Elements" Type="UInt">8191</Property>
				<Property Name="Type" Type="UInt">2</Property>
				<Property Name="Type Descriptor" Type="Str">100080000000000100094002000349313600010000000000000000</Property>
			</Item>
			<Item Name="ReadIO.loop.fpga.vi" Type="VI" URL="../FPGA/ReadIO.loop.fpga.vi">
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			</Item>
			<Item Name="WaitForEdge.fpga.vi" Type="VI" URL="../FPGA/WaitForEdge.fpga.vi">
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			</Item>
			<Item Name="WaitForEdge.better.fpga.vi" Type="VI" URL="../FPGA/WaitForEdge.better.fpga.vi">
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			</Item>
			<Item Name="WriteIO.fifodirect.fpga.vi" Type="VI" URL="../FPGA/WriteIO.fifodirect.fpga.vi">
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			</Item>
			<Item Name="WriteIO.a2dac.fpga.vi" Type="VI" URL="../FPGA/WriteIO.a2dac.fpga.vi">
				<Property Name="configString.guid" Type="Str">{05A58205-DE64-4D59-8376-5D052A6DA1A2}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=bool{1177BCDD-28E4-47F3-99D6-39F4F5F76854}Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16{1BBFC43A-704E-482E-A473-8A6DC121F385}Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16{28083CB5-4426-4CF0-A701-48DEC1CF8810}Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16{2CBAD60D-A6B7-4F1B-8012-B32594A785D4}Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16{2CE70F60-483E-4E6F-85A6-64EDBBD5D866}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=bool{3E1606A2-44BF-49C9-AA3F-11BC000C34CE}Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16{3ED7DCBB-5431-4C12-BB4F-398727CB52DD}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=bool{4186D02A-1DD5-4D55-BC93-B73158B23419}Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16{4BDDF482-2523-490F-9F84-1547DAC8EA91}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=bool{55FC8392-A65A-4E41-83FA-BD831CB8D120}"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"{5852CF2E-EFB8-4BB7-9EDE-A8A0BBD78EAE}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=bool{5D13A86F-FB17-4EA9-90FB-B7E4398C1649}Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16{60DA92AE-E34A-4274-A3C8-151C3133D2C9}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=bool{6794E3B5-A5F2-49A5-9DF7-2784C103F7E5}Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16{6BB8E99D-92F3-43BE-AE77-B50D7A69EFA1}Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16{7A5B5600-DF76-4A3C-AF6F-CC5393CEF550}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=bool{805477AD-0E4B-45DD-A2F6-63474B371F9E}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=bool{8697EC68-BE40-47CB-BBFE-8C93248A3D85}Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16{87646D82-6E41-4317-BE09-D56D3605B482}Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16{91F1C3D5-7D5A-40DB-8BF9-F24512355DBA}Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16{922C68B7-7F77-4C05-A15F-EE96A85F9822}"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{A48EAC88-5D39-4A6D-88EF-A80AAE6AB777}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=bool{A8962FFB-6132-40E5-A46F-48FFC78B12D1}Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16{AFD10E7B-0219-43A4-A538-71371F66E384}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=bool{B055064E-A2BE-4C19-ABEA-78F4696980C9}Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16{BAEE6565-AE5D-4482-B882-9A3136CD7427}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=bool{C1007F51-C59D-4DEC-99A1-69E4369E8F3D}Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16{CCF226E6-86A4-486C-9B18-6B1C4DD012BF}Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16{D0F5F4F2-46DD-471E-90E7-7DA050C608D7}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=bool{D82F7B78-E838-485F-B9E3-3670C1EF2AA6}ResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427E{D924E5B9-0AE1-4803-97A5-42795034A572}"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"{E6AE1B54-BDE2-49B7-A27C-C927A5C6725B}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=bool{E70A9FD4-994C-407B-AE68-B9DDCEB983AC}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=bool{F4AD1C19-B768-4FB9-8F46-A483C91AEBBF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=bool{F5ECD9E1-A82F-4ECF-A7AD-B2F01A61A7DF}ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolPCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGA</Property>
				<Property Name="configString.name" Type="Str">40 MHz Onboard ClockResourceName=40 MHz Onboard Clock;TopSignalConnect=Clk40;ClockSignalName=Clk40;MinFreq=40000000.000000;MaxFreq=40000000.000000;VariableFreq=0;NomFreq=40000000.000000;PeakPeriodJitter=250.000000;MinDutyCycle=50.000000;MaxDutyCycle=50.000000;Accuracy=100.000000;RunTime=0;SpreadSpectrum=0;GenericDataHash=D41D8CD98F00B204E9800998ECF8427EConnector0/AI0Arbitration=AlwaysArbitrate;resource=/Connector0/AI0;0;ReadMethodType=I16Connector0/AI1Arbitration=AlwaysArbitrate;resource=/Connector0/AI1;0;ReadMethodType=I16Connector0/AI2Arbitration=AlwaysArbitrate;resource=/Connector0/AI2;0;ReadMethodType=I16Connector0/AI3Arbitration=AlwaysArbitrate;resource=/Connector0/AI3;0;ReadMethodType=I16Connector0/AI4Arbitration=AlwaysArbitrate;resource=/Connector0/AI4;0;ReadMethodType=I16Connector0/AI5Arbitration=AlwaysArbitrate;resource=/Connector0/AI5;0;ReadMethodType=I16Connector0/AI6Arbitration=AlwaysArbitrate;resource=/Connector0/AI6;0;ReadMethodType=I16Connector0/AI7Arbitration=AlwaysArbitrate;resource=/Connector0/AI7;0;ReadMethodType=I16Connector0/AO0Arbitration=AlwaysArbitrate;resource=/Connector0/AO0;0;WriteMethodType=I16Connector0/AO1Arbitration=AlwaysArbitrate;resource=/Connector0/AO1;0;WriteMethodType=I16Connector0/AO2Arbitration=AlwaysArbitrate;resource=/Connector0/AO2;0;WriteMethodType=I16Connector0/AO3Arbitration=AlwaysArbitrate;resource=/Connector0/AO3;0;WriteMethodType=I16Connector0/AO4Arbitration=AlwaysArbitrate;resource=/Connector0/AO4;0;WriteMethodType=I16Connector0/AO5Arbitration=AlwaysArbitrate;resource=/Connector0/AO5;0;WriteMethodType=I16Connector0/AO6Arbitration=AlwaysArbitrate;resource=/Connector0/AO6;0;WriteMethodType=I16Connector0/AO7Arbitration=AlwaysArbitrate;resource=/Connector0/AO7;0;WriteMethodType=I16Connector0/DIO0ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO0;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO10ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO10;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO11ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO11;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO12ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO12;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO13ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO13;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO14ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO14;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO15ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO15;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO1ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO1;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO2ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO2;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO3ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO3;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO4ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO4;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO5ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO5;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO6ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO6;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO7ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO7;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO8ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO8;0;ReadMethodType=bool;WriteMethodType=boolConnector0/DIO9ArbitrationForOutputData=NeverArbitrate;ArbitrationForOutputEnable=NeverArbitrate;NumberOfSyncRegistersForOutputData=1;NumberOfSyncRegistersForOutputEnable=1;NumberOfSyncRegistersForReadInProject=Auto;resource=/Connector0/DIO9;0;ReadMethodType=bool;WriteMethodType=boolInputMatrixFIFO"ControlLogic=0;NumberOfElements=8191;Type=2;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;InputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"OutputMatrixFIFO"ControlLogic=0;NumberOfElements=8197;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;OutputMatrixFIFO;DataType=100080000000000100094002000349313600010000000000000000;DisableOnOverflowUnderflow=FALSE"PCIe-7852R/Clk40/falsefalseFPGA_EXECUTION_MODEFPGA_TARGETFPGA_TARGET_FAMILYVIRTEX5TARGET_TYPEFPGATriggerPlayFIFO"ControlLogic=0;NumberOfElements=37;Type=1;ReadArbs=Arbitrate if Multiple Requestors Only;ElementsPerRead=1;WriteArbs=Arbitrate if Multiple Requestors Only;ElementsPerWrite=1;Implementation=2;TriggerPlayFIFO;DataType=1000800000000001000940030003493332000100000000000000000000;DisableOnOverflowUnderflow=FALSE"</Property>
			</Item>
			<Item Name="Dependencies" Type="Dependencies">
				<Item Name="niFpgaSctlEmulationGetInTimedLoop.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationGetInTimedLoop.vi"/>
				<Item Name="niFpgaSetErrorForExecOnDevCompSimple.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/errors/niFpgaSetErrorForExecOnDevCompSimple.vi"/>
				<Item Name="niFpgaGetScratchAppInstance.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/eio/common/niFpgaGetScratchAppInstance.vi"/>
				<Item Name="nirviEmuReportErrorAndStop.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/eio/common/nirviEmuReportErrorAndStop.vi"/>
				<Item Name="niFpgaSctlEmulationClkInfo.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationClkInfo.ctl"/>
				<Item Name="niFpgaSctlEmulationSchedulerRegClks.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerRegClks.vi"/>
				<Item Name="nirviTagForDefaultClock.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/ClientSDK/Core/TimingSources/Configuration/Public/nirviTagForDefaultClock.vi"/>
				<Item Name="niFpgaSctlEmulationConstants.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationConstants.vi"/>
				<Item Name="niFpgaGenCallStack.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/errors/niFpgaGenCallStack.vi"/>
				<Item Name="nirviFillInErrorInfo.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/errors/nirviFillInErrorInfo.vi"/>
				<Item Name="niFpgaSctlEmulationIdMgrCmd.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationIdMgrCmd.ctl"/>
				<Item Name="niFpgaSctlEmulationIdMgr.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationIdMgr.vi"/>
				<Item Name="niFpgaSctlEmulationSchedulerHandleRollover.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerHandleRollover.vi"/>
				<Item Name="nirvimemoryEmulationManagerCacheLock_Operations.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCacheLock_Operations.ctl"/>
				<Item Name="nirvimemoryEmulationManagerCacheLock.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCacheLock.vi"/>
				<Item Name="nirvimemoryEmulationManagerCache_ReleaseExclusive.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache_ReleaseExclusive.vi"/>
				<Item Name="nirvimemoryEmulationManagerCache_Operations.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache_Operations.ctl"/>
				<Item Name="nirvimemoryEmulationManagerCache.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache.vi"/>
				<Item Name="nirvimemoryEmulationManagerCache_GetValue.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache_GetValue.vi"/>
				<Item Name="nirvimemoryEmulationManagerCache_MakeExclusive.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache_MakeExclusive.vi"/>
				<Item Name="nirvimemoryEmulationManagerCache_SetValue.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Memory/Memory_Emulation/nirvimemoryEmulationManagerCache_SetValue.vi"/>
				<Item Name="niFpgaSctlEmulationFifoFullMgr.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationFifoFullMgr.vi"/>
				<Item Name="niFpgaSctlEmulationSharedResTypes.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSharedResTypes.ctl"/>
				<Item Name="niFpgaSctlEmulationSharedResource.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSharedResource.ctl"/>
				<Item Name="niFpgaSctlEmulationSharedResMgrCmd.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSharedResMgrCmd.ctl"/>
				<Item Name="niFpgaSctlEmulationResourceMgr.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationResourceMgr.vi"/>
				<Item Name="nirviReportUnexpectedCaseInternalErrorHelper.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/errors/nirviReportUnexpectedCaseInternalErrorHelper.vi"/>
				<Item Name="nirviReportUnexpectedCaseInternalError (String).vi" Type="VI" URL="/&lt;vilib&gt;/rvi/errors/nirviReportUnexpectedCaseInternalError (String).vi"/>
				<Item Name="niFpgaSctlEmulationSchedulerUnRegClks.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerUnRegClks.vi"/>
				<Item Name="niFpgaSctlEmulationSchedulerGenSchedule.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerGenSchedule.vi"/>
				<Item Name="niFpgaSctlEmulationSchedulerState.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerState.ctl"/>
				<Item Name="niFpgaSctlEmulationSchedulerCommand.ctl" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationSchedulerCommand.ctl"/>
				<Item Name="niFpgaSctlEmulationScheduler.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationScheduler.vi"/>
				<Item Name="niFpgaSctlEmulationGlobalWrite.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationGlobalWrite.vi"/>
				<Item Name="niFpgaSctlEmulationRegisterWithScheduler.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaSctlEmulationRegisterWithScheduler.vi"/>
				<Item Name="niFpgaEmulationVisToLoad.vi" Type="VI" URL="/&lt;vilib&gt;/rvi/Emulation/niFpgaEmulationVisToLoad.vi"/>
			</Item>
			<Item Name="Build Specifications" Type="Build">
				<Item Name="Direttore.fpga" Type="{F4C5E96F-7410-48A5-BB87-3559BC9B167F}">
					<Property Name="AllowEnableRemoval" Type="Bool">false</Property>
					<Property Name="BuildSpecDecription" Type="Str"></Property>
					<Property Name="BuildSpecName" Type="Str">Direttore.fpga</Property>
					<Property Name="Comp.BitfileName" Type="Str">Direttore.lvbitx</Property>
					<Property Name="Comp.CustomXilinxParameters" Type="Str"></Property>
					<Property Name="Comp.MaxFanout" Type="Int">-1</Property>
					<Property Name="Comp.RandomSeed" Type="Bool">false</Property>
					<Property Name="Comp.Version.Build" Type="Int">0</Property>
					<Property Name="Comp.Version.Fix" Type="Int">0</Property>
					<Property Name="Comp.Version.Major" Type="Int">1</Property>
					<Property Name="Comp.Version.Minor" Type="Int">0</Property>
					<Property Name="Comp.VersionAutoIncrement" Type="Bool">false</Property>
					<Property Name="Comp.Xilinx.DesignStrategy" Type="Str">balanced</Property>
					<Property Name="Comp.Xilinx.MapEffort" Type="Str">high(timing)</Property>
					<Property Name="Comp.Xilinx.ParEffort" Type="Str">standard</Property>
					<Property Name="Comp.Xilinx.SynthEffort" Type="Str">normal</Property>
					<Property Name="Comp.Xilinx.SynthGoal" Type="Str">speed</Property>
					<Property Name="Comp.Xilinx.UseRecommended" Type="Bool">true</Property>
					<Property Name="DefaultBuildSpec" Type="Bool">true</Property>
					<Property Name="DestinationDirectory" Type="Path">FPGA Bitfiles</Property>
					<Property Name="ProjectPath" Type="Path">/C/Users/myerslab/workspace/Direttore/labview/direttore/Direttore.lvproj</Property>
					<Property Name="RelativePath" Type="Bool">true</Property>
					<Property Name="RunWhenLoaded" Type="Bool">false</Property>
					<Property Name="SupportDownload" Type="Bool">true</Property>
					<Property Name="SupportResourceEstimation" Type="Bool">true</Property>
					<Property Name="TargetName" Type="Str">FPGA Target</Property>
					<Property Name="TopLevelVI" Type="Ref">/My Computer/FPGA Target/Direttore.fpga.vi</Property>
				</Item>
			</Item>
		</Item>
		<Item Name="Dependencies" Type="Dependencies">
			<Item Name="vi.lib" Type="Folder">
				<Item Name="Error Cluster From Error Code.vi" Type="VI" URL="/&lt;vilib&gt;/Utility/error.llb/Error Cluster From Error Code.vi"/>
				<Item Name="NI_AALBase.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/NI_AALBase.lvlib"/>
				<Item Name="NI_AALPro.lvlib" Type="Library" URL="/&lt;vilib&gt;/Analysis/NI_AALPro.lvlib"/>
			</Item>
			<Item Name="Direttore.lvbitx" Type="Document" URL="../FPGA Bitfiles/Direttore.lvbitx"/>
			<Item Name="DirettoreReadBack.vi" Type="VI" URL="../Lib/DirettoreReadBack.vi"/>
			<Item Name="lvanlys.dll" Type="Document" URL="/&lt;resource&gt;/lvanlys.dll"/>
			<Item Name="NiFpgaLv.dll" Type="Document" URL="NiFpgaLv.dll">
				<Property Name="NI.PreserveRelativePath" Type="Bool">true</Property>
			</Item>
		</Item>
		<Item Name="Build Specifications" Type="Build">
			<Item Name="Direttore" Type="EXE">
				<Property Name="App_copyErrors" Type="Bool">true</Property>
				<Property Name="App_INI_aliasGUID" Type="Str">{EB62BD64-A5AA-4CBD-9D78-CD29902E8002}</Property>
				<Property Name="App_INI_GUID" Type="Str">{0F7FDE6B-4717-46E0-818B-61B0A5ACB9E8}</Property>
				<Property Name="Bld_buildCacheID" Type="Str">{373C0898-8C8B-44EF-8CB7-C288A085C746}</Property>
				<Property Name="Bld_buildSpecName" Type="Str">Direttore</Property>
				<Property Name="Bld_excludeLibraryItems" Type="Bool">true</Property>
				<Property Name="Bld_excludePolymorphicVIs" Type="Bool">true</Property>
				<Property Name="Bld_localDestDir" Type="Path">../builds/NI_AB_PROJECTNAME/Direttore</Property>
				<Property Name="Bld_localDestDirType" Type="Str">relativeToCommon</Property>
				<Property Name="Bld_modifyLibraryFile" Type="Bool">true</Property>
				<Property Name="Bld_previewCacheID" Type="Str">{A7CC769F-C784-41F3-94DD-F3BDE7073F9F}</Property>
				<Property Name="Destination[0].destName" Type="Str">Direttore.exe</Property>
				<Property Name="Destination[0].path" Type="Path">../builds/NI_AB_PROJECTNAME/Direttore/Direttore.exe</Property>
				<Property Name="Destination[0].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="Destination[0].type" Type="Str">App</Property>
				<Property Name="Destination[1].destName" Type="Str">Support Directory</Property>
				<Property Name="Destination[1].path" Type="Path">../builds/NI_AB_PROJECTNAME/Direttore/data</Property>
				<Property Name="DestinationCount" Type="Int">2</Property>
				<Property Name="Exe_cmdLineArgs" Type="Bool">true</Property>
				<Property Name="Exe_iconItemID" Type="Ref">/My Computer/Direttore.ico</Property>
				<Property Name="Source[0].itemID" Type="Str">{32249906-1815-4393-A119-6AAC428F33FC}</Property>
				<Property Name="Source[0].type" Type="Str">Container</Property>
				<Property Name="Source[1].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[1].itemID" Type="Ref">/My Computer/Direttore.vi</Property>
				<Property Name="Source[1].properties[0].type" Type="Str">Show horizontal scroll bar</Property>
				<Property Name="Source[1].properties[0].value" Type="Bool">false</Property>
				<Property Name="Source[1].properties[1].type" Type="Str">Show vertical scroll bar</Property>
				<Property Name="Source[1].properties[1].value" Type="Bool">false</Property>
				<Property Name="Source[1].properties[2].type" Type="Str">Show toolbar</Property>
				<Property Name="Source[1].properties[2].value" Type="Bool">false</Property>
				<Property Name="Source[1].properties[3].type" Type="Str">Allow user to close window</Property>
				<Property Name="Source[1].properties[3].value" Type="Bool">false</Property>
				<Property Name="Source[1].properties[4].type" Type="Str">Window run-time position</Property>
				<Property Name="Source[1].properties[4].value" Type="Str">Unchanged</Property>
				<Property Name="Source[1].properties[5].type" Type="Str">Show menu bar</Property>
				<Property Name="Source[1].properties[5].value" Type="Bool">false</Property>
				<Property Name="Source[1].propertiesCount" Type="Int">6</Property>
				<Property Name="Source[1].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[1].type" Type="Str">VI</Property>
				<Property Name="SourceCount" Type="Int">2</Property>
				<Property Name="TgtF_autoIncrement" Type="Bool">true</Property>
				<Property Name="TgtF_companyName" Type="Str">MPI-CBG</Property>
				<Property Name="TgtF_fileDescription" Type="Str">Direttore</Property>
				<Property Name="TgtF_fileVersion.build" Type="Int">6</Property>
				<Property Name="TgtF_fileVersion.major" Type="Int">1</Property>
				<Property Name="TgtF_internalName" Type="Str">Direttore</Property>
				<Property Name="TgtF_legalCopyright" Type="Str">Copyright © 2013 MPI-CBG</Property>
				<Property Name="TgtF_productName" Type="Str">Direttore</Property>
				<Property Name="TgtF_targetfileGUID" Type="Str">{198B486A-6A16-41F7-AF3E-BF88C8F3C32F}</Property>
				<Property Name="TgtF_targetfileName" Type="Str">Direttore.exe</Property>
			</Item>
			<Item Name="Direttore Library" Type="DLL">
				<Property Name="App_INI_aliasGUID" Type="Str">{DA629625-4A5E-4F3C-9921-F3CED96B1A90}</Property>
				<Property Name="App_INI_GUID" Type="Str">{E1B52464-259B-41FE-AE3E-B25C9EB41E6D}</Property>
				<Property Name="Bld_buildCacheID" Type="Str">{F8D8A806-D2DB-4060-9DD2-0809AA978DC7}</Property>
				<Property Name="Bld_buildSpecName" Type="Str">Direttore Library</Property>
				<Property Name="Bld_excludeLibraryItems" Type="Bool">true</Property>
				<Property Name="Bld_excludePolymorphicVIs" Type="Bool">true</Property>
				<Property Name="Bld_localDestDir" Type="Path">../lib</Property>
				<Property Name="Bld_localDestDirType" Type="Str">relativeToCommon</Property>
				<Property Name="Bld_modifyLibraryFile" Type="Bool">true</Property>
				<Property Name="Bld_previewCacheID" Type="Str">{F53697A8-7934-446D-BFD4-3C8471ACBBD4}</Property>
				<Property Name="Destination[0].destName" Type="Str">Direttore.dll</Property>
				<Property Name="Destination[0].path" Type="Path">../lib/NI_AB_PROJECTNAME.dll</Property>
				<Property Name="Destination[0].preserveHierarchy" Type="Bool">true</Property>
				<Property Name="Destination[0].type" Type="Str">App</Property>
				<Property Name="Destination[1].destName" Type="Str">Support Directory</Property>
				<Property Name="Destination[1].path" Type="Path">../lib/data</Property>
				<Property Name="DestinationCount" Type="Int">2</Property>
				<Property Name="Dll_headerGUID" Type="Str">{3D78FDA9-0FE0-49F9-9EA8-3B076B83F708}</Property>
				<Property Name="Dll_libGUID" Type="Str">{965075C4-5E14-42A5-9522-AB632573C801}</Property>
				<Property Name="Source[0].itemID" Type="Str">{2764D49C-9E1C-494C-AD8E-AD6DA9C699CF}</Property>
				<Property Name="Source[0].type" Type="Str">Container</Property>
				<Property Name="Source[1].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[1].itemID" Type="Ref">/My Computer/Lib/DirettoreClose.vi</Property>
				<Property Name="Source[1].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[1].type" Type="Str">ExportedVI</Property>
				<Property Name="Source[2].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[2].itemID" Type="Ref">/My Computer/Lib/DirettoreOpen.vi</Property>
				<Property Name="Source[2].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[2].type" Type="Str">ExportedVI</Property>
				<Property Name="Source[3].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[3].itemID" Type="Ref">/My Computer/Lib/DirettoreStart.vi</Property>
				<Property Name="Source[3].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[3].type" Type="Str">ExportedVI</Property>
				<Property Name="Source[4].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoDir" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoName" Type="Str">return value</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[0]VIProtoPassBy" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoInputIdx" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoName" Type="Str">FPGAReference</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[1]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoDir" Type="Int">3</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoName" Type="Str">MatricesArrayLength</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[10]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoDir" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoName" Type="Str">SpaceLeftInQueue</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoOutputIdx" Type="Int">4</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[11]VIProtoPassBy" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]CallingConv" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]Name" Type="Str">DirettorePlay</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoDir" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoName" Type="Str">ErrorOut</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoOutputIdx" Type="Int">15</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[12]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoInputIdx" Type="Int">7</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoLenInput" Type="Int">3</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoName" Type="Str">DeltaTimeArray</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[2]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoDir" Type="Int">3</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoName" Type="Str">DeltaTimeArrayLength</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[3]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoInputIdx" Type="Int">9</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoLenInput" Type="Int">5</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoName" Type="Str">NumberOfTimePointsToPlayArray</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[4]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoDir" Type="Int">3</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoName" Type="Str">NumberofTimePointsToPlayArrayLength</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[5]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoInputIdx" Type="Int">11</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoLenInput" Type="Int">7</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoName" Type="Str">SyncArray</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[6]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoDir" Type="Int">3</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoInputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoName" Type="Str">SyncArrayLength</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[7]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoInputIdx" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoLenInput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoName" Type="Str">NumberOfMatrices</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[8]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoDir" Type="Int">0</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoInputIdx" Type="Int">5</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoLenInput" Type="Int">10</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoLenOutput" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoName" Type="Str">MatricesArray</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoOutputIdx" Type="Int">-1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfo[9]VIProtoPassBy" Type="Int">1</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfoCPTM" Type="Bin">%A#!"!!!!!]#\E"Q!"1!!!!!&amp;E:12U%A37ZU:8*G97.F)%2Z&lt;G&amp;N;7-!%A#!"!!!!"U!%%"1!!!*5G6H;8.U:8*T!"^!&amp;A!"%%^V&gt;("V&gt;%VB&gt;(*J?%:*2E]!!!2/97VF!!!*1!)!!UER.A!L1"9!!2*O;5:Q:W&amp;)&lt;X.U6'^598*H:81!!!Z*&lt;8"M:7VF&lt;H2B&gt;'FP&lt;A!!%%"1!!-!!1!#!!-$2%V"!"V!&amp;A!"$V2S;7&gt;H:8*1&lt;'&amp;Z2EF'4Q!%4G&amp;N:1!!#5!$!!.*-T)!%%"1!!-!"1!'!!-$2%V"!"B!5!!#!!1!"QR%45%A1WBB&lt;GZF&lt;(-!!"&gt;!&amp;A!"#6*F971A6%6%5Q!%4G&amp;N:1!!&amp;U!7!!%*1W^O&lt;G6D&gt;'^S!!2/97VF!!!&amp;!!9!!"&gt;!&amp;A!""7FO=(6U!!F%;8*F9X2J&lt;WY!'U!7!!%)=G6R&gt;7FS:71!!!BS:8&amp;V;8*F:!!!'%"1!!1!#A!,!!Q!$1F198*B&lt;76U:8)!%U!7!!%%5WRP&gt;!!!"%ZB&lt;75!!"B!5!!%!!]!#Q!-!!U*5'&amp;S97VF&gt;'6S!"6!&amp;A!""U.I97ZO:7Q!"%ZB&lt;75!!"B!5!!%!"%!#Q!-!!U*5'&amp;S97VF&gt;'6S!"F!&amp;A!"#V2&amp;2&amp;-A1GFO98*Z!!2/97VF!!!%!#%!$!"!!!(`````!"1!'5!7!!%'&lt;X6U=(6U!!!*2'FS:7.U;7^O!"B!5!!%!"-!&amp;1!7!!U*5'&amp;S97VF&gt;'6S!"F!&amp;A!"#X9Q,DEA+&amp;2&amp;2&amp;-J!!2/97VF!!!91&amp;!!"!!9!"1!&amp;A!.#6"B=G&amp;N:82F=A!;1&amp;!!"A!*!!Y!%!!3!"=!'1:.:82I&lt;W1!!"B!5!!"!"I/6'&amp;S:W6U)%VF&gt;'BP:(-!!#*!5!!$!!!!#!!&lt;&amp;5FO&gt;'6S:G&amp;D:3"%:8.D=GFQ&gt;'FP&lt;A!"!"Q!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!$5:12U&amp;3:7:F=G6O9W5!&amp;U!$!""/&gt;7VC:8*0:EVB&gt;(*J9W6T!!!%!!!!&amp;U!(!""4='&amp;D:5RF:H2*&lt;F&amp;V:86F!!!&amp;!!)!!"J!1!!"`````Q!%$5VB&gt;(*J9W6T18*S98E!"1!$!!!=1%!!!@````]!"AZ%:7RU962J&lt;76"=H*B?1!!+E"!!!(`````!!9&gt;4H6N9G6S4W:5;7VF5'^J&lt;H2T6'^1&lt;'&amp;Z18*S98E!&amp;E"!!!(`````!!9*5XFO9U&amp;S=G&amp;Z!!R!)1:T&gt;'&amp;U&gt;8-!!!N!!Q!%9W^E:1!!%%!Q`````Q:T&lt;X6S9W5!!":!5!!$!!I!#Q!-#%6S=G^S4X6U!!"M!0!!%!!!!!%!!A!#!!-!"1!#!!=!!A!)!!)!#1!#!!)!!A!.!Q!"#!!!#!!!!!I!!!!!!!!!!!!!!!E!!!))!!!!!!!!!AA!!!!!!!!##!!!!!!!!!))!!!!!!!!!!!!!!!!!!!!#1!!!!!"!!Y</Property>
				<Property Name="Source[4].ExportedVI.VIProtoInfoVIProtoItemCount" Type="Int">13</Property>
				<Property Name="Source[4].itemID" Type="Ref">/My Computer/Lib/DirettorePlay.vi</Property>
				<Property Name="Source[4].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[4].type" Type="Str">ExportedVI</Property>
				<Property Name="Source[5].destinationIndex" Type="Int">0</Property>
				<Property Name="Source[5].itemID" Type="Ref">/My Computer/Lib/DirettoreStop.vi</Property>
				<Property Name="Source[5].sourceInclusion" Type="Str">TopLevel</Property>
				<Property Name="Source[5].type" Type="Str">ExportedVI</Property>
				<Property Name="SourceCount" Type="Int">6</Property>
				<Property Name="TgtF_autoIncrement" Type="Bool">true</Property>
				<Property Name="TgtF_companyName" Type="Str">MPI-CBG</Property>
				<Property Name="TgtF_fileDescription" Type="Str">Direttore Library</Property>
				<Property Name="TgtF_fileVersion.build" Type="Int">50</Property>
				<Property Name="TgtF_fileVersion.major" Type="Int">1</Property>
				<Property Name="TgtF_internalName" Type="Str">Direttore Library</Property>
				<Property Name="TgtF_legalCopyright" Type="Str">Copyright © 2013 MPI-CBG</Property>
				<Property Name="TgtF_productName" Type="Str">Direttore Library</Property>
				<Property Name="TgtF_targetfileGUID" Type="Str">{CCDA94C5-E3D0-4785-9F57-CB71151F8034}</Property>
				<Property Name="TgtF_targetfileName" Type="Str">Direttore.dll</Property>
			</Item>
		</Item>
	</Item>
</Project>
