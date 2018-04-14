# duangsuse <=> makefile(((())))
.PHONY: prepare_source build build_all test install clean javaDoc

TINYAXML_PLUG=$(HOME)/.config/tinyaxml/
JAVAC=javac
JAVACFLAGS=
JAVADOC=javadoc
JAVADOCFLAGS=-html5
FILES=hello.java plugin_missing.java to_s.java process_sample.java
OBJS=bin/hello.class bin/plugin_missing.class bin/to_s.class bin/process_sample.class
CLASSPATH=-cp ../../build/classes/java/main/

build: prepare_source $(OBJS)
	cd bin; rm *.java # Removing temp sources

bin/hello.class: hello.java
	cd bin; $(JAVAC) $(CLASSPATH) hello.java # Compiling helloworld plugin

bin/plugin_missing.class: plugin_missing.java
	cd bin; $(JAVAC) $(CLASSPATH) plugin_missing.java # Compiling plugin missing plugin

bin/to_s.class: to_s.java
	cd bin; $(JAVAC) $(CLASSPATH) to_s.java # Compiling toString plugin

bin/process_sample.class: process_sample.java
	cd bin; $(JAVAC) $(CLASSPATH) process_sample.java # Compiling toBlank plugin

prepare_source:
	mkdir -p bin # Classes built in bin
	cp $(FILES) bin # Copy source files

# *.java: prepare_source
# 	cd bin; $(JAVAC) *

build_all: prepare_source # Recompile all files all times XD 
	cd bin; $(JAVAC) $(FILES) # Compile javas using javac

test:
	bash test.sh # Just run test.sh

install: build
	cd bin; cp *.class $(TINYAXML_PLUG) # copy plugins

clean:
	@rm -f *.class # Remove all temp classes
	rm -rf bin # Cleaning classes
	rm -rf jdoc # Cleaning documents

javaDoc:
	mkdir -p jdoc # Docs built in jdoc
	cp $(FILES) jdoc # Copy source files
	cd jdoc; $(JAVADOC) $(JAVADOCFLAGS) $(FILES) # Build document using javadoc
