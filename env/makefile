include ../common.mak

OBJECTS = torus.3d1 triangle.3d1
# hammer.3d1

.PHONY: run
run: env.obx
	$(RUNOBX) $<

env.nex env.obx: $(OBJECTS) qlka1.gfx
# phong.gfx
# 14scian.asx

$(OBJECTS): Make3D1.class
	$(JAVA) Make3D1

%.gfx: %.pcx ../PCXtoGR9.class
	$(JAVA) PCXtoGR9 $< $@

.PHONY: clean
clean:
	$(RM) env.nex env.obx qlka1.gfx phong.gfx
	$(RM) Make3D1.class $(OBJECTS)
