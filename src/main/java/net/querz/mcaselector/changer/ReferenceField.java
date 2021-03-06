package net.querz.mcaselector.changer;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.Tag;
import java.util.Map;

public class ReferenceField extends Field<Boolean> {

	// compoundtag: structure_name --> long_array
	// where each long is a x/z coordinate

	public ReferenceField() {
		super(FieldType.STRUCTURE_REFERENCE);
	}

	@Override
	public boolean parseNewValue(String s) {
		setNewValue(Boolean.parseBoolean(s));
		return true;
	}

	@Override
	public Boolean getOldValue(CompoundTag root) {
		return null;
	}

	@Override
	public void change(CompoundTag root) {
		if (!getNewValue()) {
			return;
		}

		// attempt to fix chunk coordinates of structure references

		CompoundTag references = root.getCompoundTag("Level").getCompoundTag("Structures").getCompoundTag("References");
		int xPos = root.getCompoundTag("Level").getInt("xPos");
		int zPos = root.getCompoundTag("Level").getInt("zPos");
		for (Map.Entry<String, Tag<?>> entry : references) {
			if (entry.getValue() instanceof LongArrayTag) {
				long[] structureReferences = ((LongArrayTag) entry.getValue()).getValue();

				for (int i = 0; i < structureReferences.length; i++) {
					int x = (int) (structureReferences[i]);
					int z = (int) (structureReferences[i] >> 32);
					if (Math.abs(x - xPos) > 8 || Math.abs(z - zPos) > 8) {
						structureReferences[i] = ((long) zPos & 0xFFFFFFFFL) << 32 | (long) xPos & 0xFFFFFFFFL;
					}
				}
			}
		}
	}

	@Override
	public void force(CompoundTag root) {
		change(root);
	}
}
