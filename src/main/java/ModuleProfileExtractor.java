import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 21/04/2015.
 */
public class ModuleProfileExtractor {


    public OWLOntology getMireotBasic(Set<String> targetClassNames, String sourceOntology, Set<String> ontologyLocations) {

        try {
            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLClass> namedParents = new HashSet<OWLClass>();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
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
            IRI activeOntologyIRI = IRI.create(sourceOntology);
            for (String target : targetClassNames) {
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);
                OWLClass targetOWLClass = factory.getOWLClass(targetIRI);

                //get named parents of target class
                namedParents = mireotManager.getNamedClassParents(manager, activeOntologyIRI, targetIRI);

                //add named classes to ontology
                for (OWLClass parentClass : namedParents) {
                    OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(parentClass);

                    OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetOWLClass, parentClass);

                    tempManager.addAxiom(tempOntology, namedParentAxiom);
                    tempManager.addAxiom(tempOntology, subclassAxiom);
                }

                //get annotations
                annotations = mireotManager.getClassAnnotations(manager, activeOntologyIRI, targetIRI);

                for (OWLAnnotation a : annotations) {
                    OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(targetIRI, a);
                    tempManager.addAxiom(tempOntology, annotationAxiom);
                }

            }
            //get the ontology
            OWLOntology onto = tempManager.getOntology(iriTemp);

            return tempManager.getOntology(iriTemp);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        //if all else fails
        return null;
    }


    public OWLOntology getMireotFull(Set<String> targetClassNames, String sourceOntology, Set<String> ontologyLocations) {

        try {

            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
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

            //iterate through target classes and extract full mireot
            IRI activeOntologyIRI = IRI.create(sourceOntology);
            for (String target : targetClassNames) {

                System.out.println("Attempting to extract full mireot for " + target);
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);

                //get mireot ontology of target
                mireotManager.getNamedClassParentsToRoot(manager, activeOntologyIRI, targetIRI, tempOntology);

                //add annotations for all named classes in the new module
                for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                    Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, activeOntologyIRI, moduleClass.getIRI());

                    //if annotations have been extracted add them to ontology module
                    if (!tempAnnotations.isEmpty()) {
                        for (OWLAnnotation a : tempAnnotations) {
                            OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                            tempManager.addAxiom(tempOntology, annotationAxiom);
                        }

                    }

                }//end for


            }

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public OWLOntology getMireotMerge(Set<String> targetClassNames, String sourceOntology, String targetOntology, Set<String> ontologyLocations) {

        try {

            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
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

            //iterate through target classes and extract full mireot
            IRI activeOntologyIRI = IRI.create(sourceOntology);
            IRI targetOntologyIRI = IRI.create(targetOntology);
            for (String target : targetClassNames) {

                System.out.println("Attempting to extract full mireot for " + target);
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);

                //get mireot ontology of target
                mireotManager.getNamedClassParentsToExistingClass(manager, activeOntologyIRI, targetOntologyIRI, targetIRI, tempOntology);

                //add annotations for all named classes in the new module
                for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                    Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, activeOntologyIRI, moduleClass.getIRI());

                    //if annotations have been extracted add them to ontology module
                    if (!tempAnnotations.isEmpty()) {
                        for (OWLAnnotation a : tempAnnotations) {
                            OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                            tempManager.addAxiom(tempOntology, annotationAxiom);
                        }

                    }

                }//end for

            }

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


    public OWLOntology getPartialClosure(Set<String> targetClassNames, String sourceOntology, Set<String> ontologyLocations) {

        try {

            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
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

            //iterate through target classes and extract partial closure
            IRI sourceOntologyIRI = IRI.create(sourceOntology);
            for (String target : targetClassNames) {

                System.out.println("Attempting to extract partial closure for " + target);
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);

                //get partial closure of target
                mireotManager.getPartialClosureForNamedClass(manager, sourceOntologyIRI, targetIRI, tempOntology);

                //add annotations for named classes in the new module
                Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceOntologyIRI, targetIRI);

                //if annotations have been extracted add them to ontology module
                if (!tempAnnotations.isEmpty()) {
                    for (OWLAnnotation a : tempAnnotations) {
                        OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(targetIRI, a);
                        tempManager.addAxiom(tempOntology, annotationAxiom);
                    }
                }
            }//end for

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public OWLOntology getFullClosure(Set<String> targetClassNames, String sourceOntology, Set<String> ontologyLocations) {

        try {

            //create variables required
            MireotManager mireotManager = new MireotManager();
            OntologyIO ioManager = new OntologyIO();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
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


            //iterate through target classes and extract full closure
            IRI sourceOntologyIRI = IRI.create(sourceOntology);
            for (String target : targetClassNames) {

                System.out.println("Attempting to extract full closure for " + target);
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);

                //get mireot ontology of target
                mireotManager.getNamedClassParentsToRoot(manager, sourceOntologyIRI, targetIRI, tempOntology);

                //add annotations for all named classes in the new module
                for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                    Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceOntologyIRI, moduleClass.getIRI());
                    //if annotations have been extracted add them to ontology module
                    if (!tempAnnotations.isEmpty()) {
                        for (OWLAnnotation a : tempAnnotations) {
                            OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                            tempManager.addAxiom(tempOntology, annotationAxiom);
                        }

                    }

                }//end for

                //get partial closure of target
                mireotManager.getPartialClosureForNamedClass(manager, sourceOntologyIRI, targetIRI, tempOntology);

                //get RHS named classes for each axiom



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}