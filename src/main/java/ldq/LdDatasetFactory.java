/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ldq;

import java.io.File;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: improve factory, we have duplicate attributes of dataset object
// cannot create dataset object to be able to change directly its attributes, because we don't know its type
// What about instantiate the dataset object as any type, then do casting ?
// Or create default dataset, like default model ?

/**
 * A factory for creating LdDataset objects.
 */
public class LdDatasetFactory {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Single LOD dataset factory instance. */
	private static LdDatasetFactory factory;

	/** The LD dataset name. */
	private String name;

	/** link to sparql dataset or rdf file. */
	private String link; //

	/** The dataset prefixes. */
	private PrefixMapping prefixes;

	/** The directory path where dataset is defined. */
	private String path;

	private Model config = null;

	/** The dump. */
	static int SPARQL = 0, LOCAL = 1, DUMP = 2;

	private String defaultGraph;

    /**
     * Instantiates an {@link LdDatasetFactory} and initialize variables
     * (i.e. create prefix mapping with default prefixes defines in {@link #defaultPrefixes()}).
     * Path is automatically set to <code>"datasets/"</code>.
     */
    private LdDatasetFactory() {
        prefixes = new PrefixMappingImpl();
        defaultPrefixes();
        path = "datasets/";
    }

    /**
     * Gets the single instance of LdDatasetFactory.
     *
     * @return single instance of LdDatasetFactory
     */
    public static LdDatasetFactory getInstance() {
        return (factory == null) ? (factory = new LdDatasetFactory()) : factory;
    }

    /**
     * Set dataset name.
     *
     * @param name the name of the factory
     * @return The {@link LdDatasetFactory}.
     */
    public LdDatasetFactory name(String name) {
        this.name = name;
        return factory;
    }

    /**
     * Set dataset link.
     *
     * @param uri URI to LD dataset (sparql url, file path or Jena TDB directory
     * @return The {@link LdDatasetFactory}.
     */
    public LdDatasetFactory service(String uri) {
        this.link = uri;
        return factory;
    }
	
	public LdDatasetFactory file(String filename) {
		this.link = filename;
		return factory;
	}

	/**
	 * Set dataset namespace prefixes.
	 *
	 * @param prefixes
	 * @return The {@link LdDatasetFactory}.
	 */
	public LdDatasetFactory prefixes(PrefixMapping prefixes) {

		this.prefixes.setNsPrefixes(prefixes);
		return factory;
	}

	/**
	 * Default prefixes.
	 */
	private void defaultPrefixes() {
		Map<String, String> default_prefixes = LdDatasetBase.getDefaultPrefixes();
		prefixes.setNsPrefixes(default_prefixes);
	}
	
	
	/**
	 * set dataset directory path
	 *
	 * @param path the path
	 * @return The {@link LdDatasetFactory}.
	 */
	public LdDatasetFactory repository(String path) {
		this.path = path;
		return this;
	}
	
	/**
	 * Set default graph name.
	 * @param g name of the default graph
	 * @return The {@link LdDatasetFactory} instance
	 */
	public LdDatasetFactory defaultGraph(String g) {
		this.defaultGraph = g;
		return this;
	}

	/**
	 * Create the dataset.
	 *
	 * @return The {@link LdDataset} or <code>null</code>.
	 * @throws Exception If name or link (location) was not provided previously.
	 */
	public LdDataset create() throws Exception {

		if (name == null || link == null)
			throw new Exception("name and/or location are not provided");

		int type = datsetType(link);
		LdDataset dataset;
		switch (type) {
			case 0:
				dataset = new RemoteSparqlLdDataset();
				break;
			case 1: // TODO: TDB dataset
				dataset = new RemoteSparqlLdDataset();
				break;
			case 2:
				dataset = new RdfDumpFileLdDataset();
				break;
			case 3: // HDT file
				dataset = new HDTFileLdDataset(link);
				break;
			default:
				return null;
		}

		dataset.setName(name);
		dataset.setLink(link);
		dataset.setPrefixes(prefixes);
		dataset.setPath(path);
		dataset.setDefaultGraph(defaultGraph);

		return dataset;
	}

	/**
	 * Deduce dataset type from link value.
	 *
	 * @param link the location
	 * @return The type of dataset:<br/>
	 *   0 if link (URI) contains http or http,<br/>
	 *   1 for directory,<br/>
	 *   2 for .rdf,<br/>
	 *   3 for .hdt,<br/>
	 *   4 otherwise
	 */
	private int datsetType(String link) {
		if (link.contains("http://") || link.contains("https://"))
			return 0;
		else if (new File(link).isDirectory())
			return 1;
		else if (link.contains(".rdf"))
			return 2;
		else if (link.contains(".hdt"))
			return 3;
		else return 4;
	}

	/**
	 * Loading predefined dataset.
	 *
	 * @return the ld dataset
	 * @throws Exception if call to {@link #create()} fails.
	 */
	public LdDataset load() throws Exception {

		// Load prefixes
		PrefixMapping loaded_prefixes = LdDatasetBase.loadPrefixes(path + name);
		prefixes.setNsPrefixes(loaded_prefixes);

		// load config
		this.config = LdDatasetBase.loadConfig(path + name);
		this.link = Util.firstValueOfModelProperty(config, VOID.sparqlEndpoint).toString();
		return create();
	}
	
	/**
	 * TODO: create dataset repository that contains necessary files: config, prefixes
	 */
	public void contructLdDatasetRepository() {
		throw new RuntimeException("Not yet implemented!");
	}

}
