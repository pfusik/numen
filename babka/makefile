include ../common.mak

.PHONY: run
run: babka.obx
	$(RUNOBX) $<

babka.nex babka.obx: babkav3.hr tan.asx

tan.asx: TanLookup.class
	$(JAVA) TanLookup > tan.asx

.PHONY: clean
clean:
	$(RM) babka.nex babka.obx tan.asx TanLookup.class
