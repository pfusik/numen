include ../common.mak

.PHONY: run
run: flash.obx
	$(RUNOBX) $<

.PHONY: java
java: Test.class
	$(JAVA) Test

flash.nex flash.obx: flaship.gr9 flaship.grA flash.map
flash.obx: tunnelz.ang tunnelz.ray

flash.map flaship.gr9 flaship.grA: 200x200.pcx
tunnelz.ray tunnelz.ang flash.map flaship.gr9 flaship.grA: Test.class
	$(JAVA) Test write

Test.class: ../PCXImage.class

.PHONY: clean
clean:
	$(RM) flash.nex flash.obx
	$(RM) tunnelz.ray tunnelz.ang flash.map flaship.gr9 flaship.grA
	$(RM) Test*.class
