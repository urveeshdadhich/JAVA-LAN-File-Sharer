[Setup]
AppName=Java LAN File Sharer
AppVersion=1.0
AppPublisher=Your Name
DefaultDirName={autopf}\JavaLANFileSharer
DefaultGroupName=Java LAN File Sharer
OutputBaseFilename=JavaLANFileSharer-Setup
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
; This tells the installer to grab the JAR file and install it
; in the application directory.
Source: "LanFileSharer.j
ar"; DestDir: "{app}"

[Icons]
; This creates the Start Menu shortcut.
; It runs "javaw.exe" (the window-less Java) to avoid a black console window.
; This assumes the user has Java installed and in their PATH.
Name: "{autoprograms}\Java LAN File Sharer"; Filename: "javaw.exe"; \
    Parameters: "-jar ""{app}\LanFileSharer.jar"""; WorkingDir: "{app}"

; =================================================================
; THIS IS THE FIREWALL SOLUTION
; =================================================================

[Run]
; This section runs *after* the installation is complete.
; It runs the `netsh` command to add the firewall rule.
; `runhidden` means the user won't see the black console window.
Filename: "{sys}\netsh.exe"; \
    Parameters: "advfirewall firewall add rule name=""Java LAN Sharer (UDP)"" dir=in action=allow protocol=UDP localport=6790"; \
    Flags: runhidden

[UninstallRun]
; This section runs when the user *uninstalls* your app.
; It cleans up the firewall rule, which is very professional.
Filename: "{sys}\netsh.exe"; \
    Parameters: "advfirewall firewall delete rule name=""Java LAN Sharer (UDP)"""; \
    Flags: runhidden