//------------------------------------------------------------------------------------------------
//
//   SG Craft - Extra data saved with a chunk
//
//------------------------------------------------------------------------------------------------

package sgextensions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;

import java.util.WeakHashMap;

public class SGChunkData
{

	static WeakHashMap<Chunk, SGChunkData> map = new WeakHashMap<Chunk, SGChunkData>();

	public boolean oresGenerated;

	public static SGChunkData forChunk(Chunk chunk)
	{
		SGChunkData data = map.get(chunk);
		if (data == null)
		{
			data = new SGChunkData();
			map.put(chunk, data);
		}
		return data;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		oresGenerated = nbt.getBoolean("gcewing.sg.oresGenerated");
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("gcewing.sg.oresGenerated", oresGenerated);
	}

	public static void onChunkLoad(ChunkDataEvent.Load e)
	{
		Chunk chunk = e.getChunk();
		//System.out.printf("SGChunkData.onChunkLoad: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData data = SGChunkData.forChunk(chunk);
		data.readFromNBT(e.getData());
		if (!data.oresGenerated && SGExtensions.addOresToExistingWorlds)
		{
			System.out.printf("SGChunkData.onChunkLoad: Adding ores to chunk (%d, %d)\n", chunk.xPosition, chunk.zPosition);
			SGExtensions.naquadahOreGenerator.regenerate(chunk);
		}
	}

	public static void onChunkSave(ChunkDataEvent.Save e)
	{
		Chunk chunk = e.getChunk();
		//System.out.printf("SGChunkData.onChunkLoad: (%d, %d)\n", chunk.xPosition, chunk.zPosition);
		SGChunkData data = SGChunkData.forChunk(chunk);
		data.writeToNBT(e.getData());
	}

}
