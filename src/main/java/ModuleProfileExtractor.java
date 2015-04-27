import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.util.Collections;
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



    public OWLOntology getFullClosure(Set<String> targetClassNames, String sourceOntologyName, Set<String> ontologyLocations) {

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

            //load all ontologies
            for (String location : ontologyLocations) {
                ioManager.loadOntologyFromFileLocation(location);
            }

            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();
            Set<OWLOntology> loadedOntologies = manager.getOntologies();
            System.out.println("Loaded ontologies " + loadedOntologies.toString());

            //iterate through target classes and extract full closure
            IRI sourceOntologyIRI = IRI.create(sourceOntologyName);
            for (String target : targetClassNames) {

                System.out.println("Attempting to extract full closure for " + target);
                //create iri and owlclass of target class
                IRI targetIRI = IRI.create(target);

                //get parent classes for target class
                mireotManager.getNamedClassParentsToRoot(manager, sourceOntologyIRI, targetIRI, tempOntology);

                //get partial closure of target class
                mireotManager.getPartialClosureForNamedClass(manager, sourceOntologyIRI, targetIRI, tempOntology);

                //grab the source ontology and the owlclass for the target IRI
                OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);
                OWLDataFactory factory = manager.getOWLDataFactory();
                OWLClass targetClass = factory.getOWLClass(targetIRI);

                //create somewhere to store the named classes in the axioms
                Set<OWLClass> namedClassesInAxiom = new HashSet<OWLClass>();

                //get RHS named classes for each axiom on target class
                for (OWLSubClassOfAxiom ax : sourceOntology.getSubClassAxiomsForSubClass(targetClass)) {
                    OWLClassExpression superCls = ax.getSuperClass();

                    //if the axiom is not subclassof some named class (this is already covered
                    if (superCls instanceof OWLAnonymousClassExpression) {
                        for (OWLClass namedClassInAxiom : superCls.getClassesInSignature()) {
                            namedClassesInAxiom.add(namedClassInAxiom);

                        }
                    }
                }


                for(OWLClass c : namedClassesInAxiom){
                    System.out.println("Named calsses in axioms: " + c.toString());
                    mireotManager.getNamedClassParentsToRoot(manager, sourceOntologyIRI, c.getIRI(), tempOntology);

                }

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


            }

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    //inner class for visiting restrictions
    class RestrictionVisitor extends OWLClassExpressionVisitorAdapter {

        private final Set<OWLClass> processedClasses;
        private final Set<OWLObjectPropertyExpression> restrictedProperties;
        private final Set<OWLOntology> onts;

        public RestrictionVisitor(Set<OWLOntology> onts) {
            restrictedProperties = new HashSet<OWLObjectPropertyExpression>();
            processedClasses = new HashSet<OWLClass>();
            this.onts = onts;
        }

        public Set<OWLObjectPropertyExpression> getRestrictedProperties() {
            return restrictedProperties;
        }

        @Override
        public void visit(OWLClass desc) {
            if (!processedClasses.contains(desc)) {
                // If we are processing inherited restrictions then we
                // recursively visit named supers. Note that we need to keep
                // track of the classes that we have processed so that we don't
                // get caught out by cycles in the taxonomy
                processedClasses.add(desc);
                for (OWLOntology ont : onts) {
                    for (OWLSubClassOfAxiom ax : ont
                            .getSubClassAxiomsForSubClass(desc)) {
                        ax.getSuperClass().accept(this);
                    }
                }
            }
        }

        @Override
        public void visit(OWLObjectSomeValuesFrom desc) {
            // This method gets called when a class expression is an existential
            // (someValuesFrom) restriction and it asks us to visit it
            restrictedProperties.add(desc.getProperty());
        }
    }


}