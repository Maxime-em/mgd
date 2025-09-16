@echo off
start javaw -Dapplication.configuration=.\conf\configuration.properties -Dlog4j2.configurationFile=.\conf\log4j2.properties -cp lib/* --module-path lib -m org.mgd.guerres.puniques.lwjgl/org.mgd.guerres.puniques.GuerresPuniquesApplication
