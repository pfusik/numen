include ../../common.mak

LEVEL = 7
DIRNAME = 7credits
COLORS = 0

include ../level.mak

duke.nex duke.obx: text.raw

text.raw: ConvText.class text.pcx
	$(JAVA) ConvText text.pcx text.raw
