@echo off
start javaw -Dapplication.configuration=.\conf\configuration.properties -Dlog4j2.configurationFile=.\conf\log4j2.properties -cp lib/* --module-path lib --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=org.mgd.gmel.javafx --add-exports javafx.controls/com.sun.javafx.scene.control.inputmap=org.mgd.gmel.javafx -m org.mgd.gmel.javafx/org.mgd.gmel.javafx.GmelApplication
