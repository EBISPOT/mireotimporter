import org.semanticweb.owlapi.model.*;

import java.util.Set;

/**
 * Created by malone on 21/04/2015.
 */
public class ModuleProfileExtractor {



    public OWLOntology getMireotBasic(Set<String> targetClass, String activeOntology, Set<String> ontologyLocations){

        MireotManager mireotManager = new MireotManager();
        OntologyIO ioManager = new OntologyIO();

        //load all ontologies
        for(String location : ontologyLocations){
            ioManager.loadOntologyFromFileLocation(location);
        }

        //get handle to ontologies and manager they are loaded in to
        OWLOntologyManager manager = ioManager.getManager();
        Set<OWLOntology> loadedOntologies = manager.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

        //iterate through target classes and extract basic mireot
        IRI activeOntologyIRI = IRI.create(activeOntology);
        for(String target : targetClass) {
            IRI iri = IRI.create(target);

            //get named parents
            Set<OWLClass> namedParents = mireotManager.getNamedClassParents(manager, activeOntologyIRI, iri);

            //get annotations
            Set<OWLAnnotation> annotations = mireotManager.getClassAnnotations(manager, activeOntologyIRI, iri);

        }

        //todo
        return null;
    }


}
