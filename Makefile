.PHONY: test lint dist build

all: demo

build:
	gradle build

copydep:
	gradle copyDependencies

demo: copydep build
	./runAPITest.sh

demo_wrapper: copydep build
	./runImage2LabelAgent.sh
