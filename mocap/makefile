include ../common.mak

.PHONY: run
run: mocap.obx
	$(RUNOBX) $<

mocap.nex mocap.obx: dance01.asx

%.asx: %.dmp Joint.class
	$(JAVA) Joint < $< > $@

.PHONY: clean
clean:
	$(RM) mocap.nex mocap.obx dance01.asx drunk10.asx Joint.class
