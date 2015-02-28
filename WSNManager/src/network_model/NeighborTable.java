package network_model;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Timothy on 2/13/2015.
 */
public class NeighborTable implements Iterable
{
    private HashMap<Byte, NeighborEntry> _table;

    public NeighborTable()
    {
        _table = new HashMap<Byte, NeighborEntry>();
    }

    public void addRow(NeighborEntry e)
    {
        _table.put(e.row, e);
    }

    @Override
    public Iterator iterator()
    {
        return null;
    }
}
