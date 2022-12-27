@echo off
start javaw -Dapplication.configuration=.\conf\configuration.properties -cp lib/commons-logging-1.3.3.jar --module-path lib --add-exports javafx.controls/com.sun.javafx.scene.control.behavior=org.mgd.gmel.javafx --add-exports javafx.controls/com.sun.javafx.scene.control.inputmap=org.mgd.gmel.javafx -m org.mgd.gmel.javafx/org.mgd.gmel.javafx.GmelApplication
