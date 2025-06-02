@echo off
cd /d C:\JDBCLab

set JAVAFX_LIB=C:\JDBCLab\openjfx-24.0.1_windows-x64_bin-sdk\javafx-sdk-21.0.7\lib

java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml -jar CancerFXApp.jar

pause