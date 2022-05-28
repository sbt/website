\$ sdk install java \$(sdk list java | grep -o "\b8\.[0-9]*\.[0-9]*\-tem" | head -1)
\$ sdk install sbt