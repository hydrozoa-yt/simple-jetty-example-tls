rm -r dis/webapp/
mkdir dis/webapp/
cp target/*.jar dis/webapp/
mkdir dis/webapp/libs/
cp target/libs/*.jar dis/webapp/libs/
mkdir dis/webapp/res/
cp dis/config.properties dis/webapp/res/
pause