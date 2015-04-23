import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 21/04/2015.
 */
public class ModuleProfileExtractor {


    public OWLOntology getMireotBasic(Set<String> targetClass, String activeOntology, Set<String> ontologyLocations) {

        try {
            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLClass> namedParents = new HashSet<OWLClass>();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            // Let's load an ontology from the web
            IRI iriTemp = IRI
                    .create("http://mireotmodule.owl");

            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();


            //load all ontologies
            for (String location : ontologyLocations) {
                ioManager.loadOntologyFromFileLocation(location);
            }

            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();
            Set<OWLOntology> loadedOntologies = manager.getOntologies();
            System.out.println("Loaded ontologies " + loadedOntologies.toString());

            //iterate through target classes and extract basic mireot
            IRI activeOntologyIRI = IRI.create(activeOntology);
            for (String target : targetClass) {
                IRI iri = IRI.create(target);

                //get named parents
                namedParents = mireotManager.getNamedClassParents(manager, activeOntologyIRI, iri);

                //add named classes to ontology

                for (OWLClass o : namedParents) {
                    OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(o);
                    tempManager.addAxiom(tempOntology, declarationAxiom);
                }

                //get annotations
                annotations = mireotManager.getClassAnnotations(manager, activeOntologyIRI, iri);

                for (OWLAnnotation a : annotations) {
                    OWLAnnotationAxiom declarationAxiom = factory.getOWLAnnotationAssertionAxiom(iri, a);
                    tempManager.addAxiom(tempOntology, declarationAxiom);
                }

            }
            //get the ontology
            OWLOntology onto = tempManager.getOntology(iriTemp);
            System.out.println("axiom count " + onto.getAxiomCount());

            return tempManager.getOntology(iriTemp);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        //if all else fails
        return null;
    }


}
