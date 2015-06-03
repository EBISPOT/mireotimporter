import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

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

                Set<OWLImportsDeclaration> imports = tempOntology.getImportsDeclarations();

                //if there are no imports than add straight into manager
                if (imports.isEmpty()){
                    IRI ontoName = tempOntology.getOntologyID().getOntologyIRI();
                    this.manager.createOntology(tempOntology.getAxioms(), ontoName);
                }
                //otherwise merge all imports into the source ontology
                else {

                    IRI ontoName = tempOntology.getOntologyID().getOntologyIRI();
                    //create merger
                    OWLOntologyMerger merger = new OWLOntologyMerger(tempManager);
                    OWLOntology mergedOntology = merger.createMergedOntology(tempManager, IRI.create("http://temp"));

                    this.manager.createOntology(mergedOntology.getAxioms(), ontoName);
                }
            }
        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Ontology failed to load.");
            e.printStackTrace();
        }
    }



    public void loadOntologyFromFileLocationNoMergeImports(String loadLocation) {

        File f = new File(loadLocation);

        try {
            System.out.println("Attempting ontology load from" + loadLocation);
            OWLOntology ontology = this.manager.loadOntologyFromOntologyDocument(f);
            System.out.println("loaded ontology " + ontology.getOntologyID().toString());

        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Ontology failed to load.");
            e.printStackTrace();
        }
    }





    public OWLOntologyManager getManager(){
        return this.manager;
    }

    public Set<IRI> getLoadedOntologyIRIs(){

        Set<IRI> loadedOntologyIRIs = new HashSet<IRI>();

        for(OWLOntology ontology : manager.getOntologies()) {
            loadedOntologyIRIs.add(ontology.getOntologyID().getOntologyIRI());
        }

        return loadedOntologyIRIs;

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


    public IRI getTargetOntology() {
        return targetOntology;
    }

    public void setTargetOntology(IRI targetOntology) {
        this.targetOntology = targetOntology;
    }
}
