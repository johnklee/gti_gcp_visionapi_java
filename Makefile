.PHONY: test lint dist build

all: demo

build:
	gradle build

copydep:
	gradle copyDependencies

demo: copydep build
	./runAPITest.sh
