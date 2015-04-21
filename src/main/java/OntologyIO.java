import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;

/**
 * Created by malone on 27/03/2015.
 */
public class OntologyIO {


    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    /**
     * load OWL ontology into the class's manager from a file location
     *
     * @param loadLocation
     * @throws OWLOntologyCreationException
     */
    public void loadOntologyFromFileLocation (String loadLocation) {

        File f = new File(loadLocation);

        //File ordo = new File("/Users/malone/EFO/EFOInternalEBI/ExperimentalFactorOntology/ExFactorInOWL/releasecandidate/efo_ordo_module.owl");
        //manager.addIRIMapper(new SimpleIRIMapper(IRI.create("http://www.orpha.net/ontology/orphaEfoMod.owl"), IRI.create(f)));


        try {
            OWLOntology ontology = this.manager.loadOntologyFromOntologyDocument(f);
        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Ontology failed to load.");
            e.printStackTrace();
        }

        System.out.println("loaded ontology ");

    }

    /**
     * get the OWLOntologyManager used to load ontologies into for this IO class
     * @return
     */
    public OWLOntologyManager getManager(){
        return this.manager;
    }






}
