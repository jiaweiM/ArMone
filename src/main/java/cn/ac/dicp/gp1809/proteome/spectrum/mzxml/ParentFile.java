package cn.ac.dicp.gp1809.proteome.spectrum.mzxml;

/**
 * The ParentFile class contains information about parent files
 * of an mzXML file.
 *
 * @author Mathijs
 */
public class ParentFile
{
    //public final static int TYPE_RAW = 1, TYPE_PROCESSED = 2;

    protected String URI, sha1, type;


    public ParentFile(String URI, String type, String sha1)
    {
        this.URI = URI;
        this.sha1 = sha1;
        this.type = type;
    }

    /**
     * Get the URI of this file.
     */
    public String getURI()
    {
        return URI;
    }

    /**
     * Get the sha1-sum of this file.
     */
    public String getSha1()
    {
        return sha1;
    }

    /**
     * Return the type of parent file.
     * <p>
     * This value is either TYPE_RAW or TYPE_PROCESSED.
     *
     * @return the type of parent file.
     */
    public String getType()
    {
        return type;
    }

    public String toString()
    {
        return ("URI " + URI + " sha1 " + sha1 + " type " + type);
    }

}
