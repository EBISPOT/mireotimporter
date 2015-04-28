import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 27/03/2015.
 */
public class OntologyIO {


    private OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    private IRI targetOntology;
    private Set<IRI> sourceOntologies = new HashSet<IRI>();


    /**
     * load OWL ontology into the class's manager from a file location
     *
     * @param loadLocation
     * @throws OWLOntologyCreationException
     */
    public void loadOntologyFromFileLocation (String loadLocation, Boolean ignoreImports) {


        File f = new File(loadLocation);

        try {

            //if imports are to be discarded then remove from the manager
            if (ignoreImports){

                OWLOntology ontology = this.manager.loadOntologyFromOntologyDocument(f);

                System.out.println("loaded ontology " + ontology.getOntologyID().toString());

                Set<OWLOntology> imports = ontology.getImports();
                for (OWLOntology o : imports){
                    System.out.println("removing import: " + o.getOntologyID().toString());
                    manager.removeOntology(o);
                }

            }
            //otherwise we need to include all imports, merge them into this single ontology for walking up tree
            else{
                //create temp manager we'll merge into first
                OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
                //load ontology into it
                OWLOntology tempOntology = tempManager.loadOntologyFromOntologyDocument(f);



                IRI ontoName = tempOntology.getOntologyID().getOntologyIRI();
                //create merger
                OWLOntologyMerger merger = new OWLOntologyMerger(tempManager);
                OWLOntology mergedOntology = merger.createMergedOntology(tempManager, ontoName);

                this.manager.createOntology(mergedOntology.getAxioms(), ontoName);

            }

        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Ontology failed to load.");
            e.printStackTrace();
        }
    }


    /**
     * get the OWLOntologyManager used to load ontologies into for this IO class
     * @return
     */
    public OWLOntologyManager getManager(){
        return this.manager;
    }



    public void saveOntologyToFileLocation(OWLOntology ontology, String saveLocation){

        try{
                OWLOntologyManager m = ontology.getOWLOntologyManager();

                //save ontology
                m.saveOntology(ontology, IRI.create(saveLocation));
                System.out.println("ontology saved");
            }
        catch(Exception e){
            System.out.println("Ontology failed to save.");
            e.printStackTrace();
        }

    }


    public Set<IRI> getSourceOntologies() {
        return sourceOntologies;
    }


    public void setSourceOntologies(Set<IRI> sources) {
        this.sourceOntologies = sources;
    }


    public IRI getActiveOntology() {
        return targetOntology;
    }

    public void setActiveOntology(IRI activeOntology) {
        this.targetOntology = activeOntology;
    }
}
