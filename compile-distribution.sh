rm -r dis/*
cp target/*.jar dis/
mkdir dis/libs/
cp target/libs/*.jar dis/libs/
mkdir dis/keystore
read -p "Press [Enter] key to continue..."