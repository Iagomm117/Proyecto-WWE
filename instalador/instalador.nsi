!include "MUI2.nsh"

Name "Proyecto WWE"
OutFile "Instalador_ProyectoWWE.exe"
InstallDir "$PROGRAMFILES\ProyectoWWE"
RequestExecutionLevel admin


!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_LANGUAGE "Spanish"


Section "Instalar"
    SetOutPath "$INSTDIR"
    File "ProyectoWWE.jar"
    File "README.txt"
    SetOutPath $INSTDIR\lib 
    File /r "lib\"
    WriteUninstaller "$INSTDIR\Uninstall.exe"
    CreateShortCut "$DESKTOP\WWE.lnk" "$INSTDIR\ProyectoWWE.jar"
SectionEnd

Section "Uninstall"
    RMDir /r "$INSTDIR"
    Delete "$DESKTOP\WWE.lnk"
SectionEnd