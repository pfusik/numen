include ../common.mak

.PHONY: run
run: rubik.obx
	$(RUNOBX) $<

rubik.nex rubik.obx: tan.asx

tan.asx: MakeTan.class
	$(JAVA) MakeTan >tan.asx

.PHONY: clean
clean:
	$(RM) rubik.nex rubik.obx tan.asx MakeTan.class
