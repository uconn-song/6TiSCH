package network_model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Timothy on 2/13/2015.
 */
public class NeighborTable
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

}
