include ../common.mak

.PHONY: run
run: hipbump.obx
	$(RUNOBX) $<

.PHONY: java
java: Test.class
	$(JAVA) Test

hipbump.nex hipbump.obx: bumpopt.asx
hipbump.obx: bumpmap.gfx

bumpopt.asx bumpmap.gfx: Test.class tqa160_2.pcx
	$(JAVA) Test write >bumpopt.asx

Test.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) hipbump.nex hipbump.obx
	$(RM) bumpopt.asx bumpmap.gfx
	$(RM) Test*.class
