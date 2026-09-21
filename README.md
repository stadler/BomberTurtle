# BomberTurtle

A libGDX based game with bombs and turtles.

## Download and Run
```bash
curl -o bomberturtle-0.1.jar https://github.com/stadler/BomberTurtle/releases/download/0.1/bomberturtle-0.1.jar
java -XstartOnFirstThread -jar bomberturtle-0.1.jar
```

## Build
### Preparation
Install java and gradle (e.g. with Homebrew on MacOS):
```bash
brew install temurin gradle
```
### Run the game directly
```bash
gradle run
```

### Build an executable jar
```bash
gradle dist
```

### Run the executable jar
```bash
java -XstartOnFirstThread -jar ./desktop/build/libs/desktop*.jar 
```