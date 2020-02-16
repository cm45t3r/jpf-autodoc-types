::
:: batch file to run jpfadt from a command prompt
::

@echo off
set JPF_ADT=%~dp0..

java -jar "%JPF_ADT%\build\jpf-autodoc-types.jar" %*
