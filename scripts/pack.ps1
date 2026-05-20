$ErrorActionPreference = "Stop"
$Root = Resolve-Path "$PSScriptRoot\.."
$Out = Join-Path (Split-Path $Root -Parent) "AIAssistantFabric_1.21.11.zip"
if (Test-Path $Out) { Remove-Item $Out -Force }
Compress-Archive -Path (Join-Path $Root "*") -DestinationPath $Out -Force
Write-Host "Created $Out"
