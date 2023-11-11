@REM
@REM SmartSprites Project
@REM
@REM Copyright (C) 2007-2009, Stanisław Osiński.
@REM All rights reserved.
@REM
@REM Redistribution and use in source and binary forms, with or without modification,
@REM are permitted provided that the following conditions are met:
@REM
@REM - Redistributions of  source code must  retain the above  copyright notice, this
@REM   list of conditions and the following disclaimer.
@REM
@REM - Redistributions in binary form must reproduce the above copyright notice, this
@REM   list of conditions and the following  disclaimer in  the documentation  and/or
@REM   other materials provided with the distribution.
@REM
@REM - Neither the name of the SmartSprites Project nor the names of its contributors
@REM   may  be used  to endorse  or  promote  products derived   from  this  software
@REM   without specific prior written permission.
@REM
@REM - We kindly request that you include in the end-user documentation provided with
@REM   the redistribution and/or in the software itself an acknowledgement equivalent
@REM   to  the  following: "This product includes software developed by the SmartSprites
@REM   Project."
@REM
@REM THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"  AND
@REM ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT LIMITED  TO, THE IMPLIED
@REM WARRANTIES  OF  MERCHANTABILITY  AND  FITNESS  FOR  A  PARTICULAR  PURPOSE   ARE
@REM DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE  FOR
@REM ANY DIRECT, INDIRECT, INCIDENTAL,  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  DAMAGES
@REM (INCLUDING, BUT  NOT LIMITED  TO, PROCUREMENT  OF SUBSTITUTE  GOODS OR SERVICES;
@REM LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON
@REM ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT  LIABILITY,  OR TORT
@REM (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY WAY  OUT OF THE USE  OF THIS
@REM SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
@REM

@echo off

rem
rem Get directory to us
rem
set ROOT=%~dp0

rem
rem Add extra JVM options here
rem
set OPTS=-Xms64m -Xmx256m

rem
rem Build command line arguments
rem
set CMD_LINE_ARGS=%1
if ""%1""=="""" goto doneStart
shift
:setupArgs
if ""%1""=="""" goto doneStart
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setupArgs
:doneStart

rem
rem Launch SmartSprites
rem
java %OPTS% -Djava.ext.dirs="%ROOT%lib" org.carrot2.labs.smartsprites.SmartSprites %CMD_LINE_ARGS%

