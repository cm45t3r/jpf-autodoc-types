::
:: batch file to run Ant from a command prompt
::

@echo off
set JPF_ADT=%~dp0..

java -jar "%JPF_ADT%\tools\ant-launcher.jar" %*
