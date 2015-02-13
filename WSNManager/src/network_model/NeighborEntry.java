package network_model;

/**
 * Created by Timothy on 2/13/2015.
 */
public class NeighborEntry
{
    //this is basically mimicing a struct right now.
    //I might change this because it would be nice to iterate over these fields.
    public byte row;
    public byte used;
    public byte parentPreference;
    public byte stableNeighbor;
    public byte switchStabilityCounter;
    public byte addr_type;
    //public byte addr_bodyH; // not entirely clear on these. it looks like there are 5 types of addresses that could be here so I want to figure out how to interpret the type to figure out how long it will be and take that many following bytes and put them in an array.
    //public byte addr_bodyL; // the bodyH, bodyL thing is from the Python parser, but I'm not really sure how that works
    public byte[] addr_16b = new byte[16];
    public byte[] addr_64b = new byte[64];
    public byte[] addr_128b = new byte[128];
    public byte[] panid = new byte[2]; // 2 bytes
    public byte[] prefix = new byte[8]; // 8 bytes

    public byte DAGrank;
    public byte rssi;
    public byte numRx;
    public byte numTx;
    public byte numTxACK;
    public byte numWraps;
    public byte asn_4;
    public byte asn_2_3;
    public byte asn_0_1;
    public byte joinPrio;

    public NeighborEntry(){}

    @Override
    public String toString()
    {
        return new String();
    }

    @Override
    public boolean equals(Object obj)
    {
        return this.row == ((NeighborEntry)obj).row;
    }
}
