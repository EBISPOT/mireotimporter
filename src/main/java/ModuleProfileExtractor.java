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


    /**
     * Extract a basic MIREOT for a set of named classes. This corresponds to the MIREOT publication
     * in Courtot et al, (2011)
     *
     * @param targetClasses     The set of class IRIs for which the mireot module is required
     * @param ontologyLocations The set of ontology locations from which the source is contained
     * @param sourceOntologies  The set of ontologies from which the mireot should be extracted (i.e. the source ontologies)
     * @param ignoreImports     Flag to ignore the import closures on the source ontologies
     * @return
     */
    public OWLOntology getMireotBasic(Set<IRI> targetClasses, Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports) {

        try {
            //load all ontologies
            OntologyIO ioManager = this.loadAllOntologies(ontologyLocations, sourceOntologies, ignoreImports, null);
            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();
            Set<OWLClass> namedParents = new HashSet<OWLClass>();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();

            //iterate through target classes and extract basic mireot
            for (IRI sourceIRI : sourceOntologies) {
                for (IRI target : targetClasses) {

                    OWLClass targetOWLClass = factory.getOWLClass(target);

                    //get named parents of target class
                    namedParents = mireotManager.getNamedClassParents(manager, sourceIRI, target);

                    //add named classes to ontology
                    for (OWLClass parentClass : namedParents) {
                        OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(parentClass);

                        OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetOWLClass, parentClass);

                        tempManager.addAxiom(tempOntology, namedParentAxiom);
                        tempManager.addAxiom(tempOntology, subclassAxiom);
                    }

                    //get annotations
                    annotations = mireotManager.getClassAnnotations(manager, sourceIRI, target);

                    for (OWLAnnotation a : annotations) {
                        OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(target, a);
                        tempManager.addAxiom(tempOntology, annotationAxiom);
                    }

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



    /**
     * Extract a basic MIREOT for a set of named classes from an ontology(ies) loaded into an OWLOntologyManager.
     * This corresponds to the MIREOT publication in Courtot et al, (2011)
     *
     * @param targetClasses     The set of class IRIs for which the mireot module is required
     * @param manager The manager into which the ontology sources are loaded
     * @param sourceOntologies  The IRIs of set of ontologies from which the mireot should be extracted (i.e. the source ontologies)
     * @return
     */
    public OWLOntology getMireotBasic(Set<IRI> targetClasses, OWLOntologyManager manager, Set<IRI> sourceOntologies) {

        try {
            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();
            Set<OWLClass> namedParents = new HashSet<OWLClass>();
            Set<OWLAnnotation> annotations = new HashSet<OWLAnnotation>();

            //iterate through target classes and extract basic mireot
            for (IRI sourceIRI : sourceOntologies) {
                for (IRI target : targetClasses) {

                    OWLClass targetOWLClass = factory.getOWLClass(target);

                    //get named parents of target class
                    namedParents = mireotManager.getNamedClassParents(manager, sourceIRI, target);

                    //add named classes to ontology
                    for (OWLClass parentClass : namedParents) {
                        OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(parentClass);

                        OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetOWLClass, parentClass);

                        tempManager.addAxiom(tempOntology, namedParentAxiom);
                        tempManager.addAxiom(tempOntology, subclassAxiom);
                    }

                    //get annotations
                    annotations = mireotManager.getClassAnnotations(manager, sourceIRI, target);

                    for (OWLAnnotation a : annotations) {
                        OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(target, a);
                        tempManager.addAxiom(tempOntology, annotationAxiom);
                    }

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


    /**
     * Extract OWL module of mireot plus the path to root of the target classes and their annotations
     *
     * @param targetClasses The set of class IRIs for which the mireot module is required
     * @param ontologyLocations The set of ontology locations from which the source is contained
     * @param sourceOntologies The IRIs of set of ontologies from which the mireot should be extracted (i.e. the source ontologies)
     * @param ignoreImports Flag to ignore the import closures on the source ontologies
     * @return
     */
    public OWLOntology getMireotFull(Set<IRI> targetClasses, Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports) {

        try {

            //load all ontologies
            OntologyIO ioManager = this.loadAllOntologies(ontologyLocations, sourceOntologies, ignoreImports, null);
            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();

            //chain to method using loaded ontologies
            OWLOntology ontology = getMireotFull(targetClasses, manager, sourceOntologies);

            return ontology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * Extract OWL module of mireot plus the path to root of the target classes and their annotations
     *
     * @param targetClasses The set of class IRIs for which the mireot module is required
     * @param manager The manager into which the ontology sources are loaded
     * @param sourceOntologies The IRIs of set of ontologies from which the mireot should be extracted (i.e. the source ontologies)
     * @return
     */
    public OWLOntology getMireotFull(Set<IRI> targetClasses, OWLOntologyManager manager, Set<IRI> sourceOntologies) {

        try {

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();

            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                //iterate through target classes and extract full mireot
                for (IRI target : targetClasses) {

                    System.out.println("Attempting to extract full mireot for " + target);

                    //get mireot ontology of target
                    mireotManager.getNamedClassParentsToRoot(manager, sourceIRI, target, tempOntology);

                    //add annotations for all named classes in the new module
                    for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                        Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, moduleClass.getIRI());

                        //if annotations have been extracted add them to ontology module
                        if (!tempAnnotations.isEmpty()) {
                            for (OWLAnnotation a : tempAnnotations) {
                                OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                                tempManager.addAxiom(tempOntology, annotationAxiom);
                            }
                        }
                    }//end for
                }
            }

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Extract module from source ontologies based on the targetClasses. Module is built by getting parents
     * (named classes only) up to and including a parent class that exists in the targetOntology - if none of the parent
     * classes are in the targetOntology the parent classes are included up to the root class in the source ontology.
     * The method creates a module which can be merged into the targetOntology.
     *
     * @param targetClasses The set of class IRIs for which the mireot module is required
     * @param ontologyLocations The set of ontology locations from which the source and the target ontology is contained
     * @param sourceOntologies The IRIs of set of ontologies from which the module should be extracted (i.e. the source ontologies)
     * @param targetOntologyIRI The ID of the ontology from which the target ontology classes are supplied to build the module
     * @param ignoreImports Flag to ignore the import closures on the source and target ontologies
     * @return
     */
    public OWLOntology getMireotMerge(Set<IRI> targetClasses, Set<String> ontologyLocations, Set<IRI> sourceOntologies, IRI targetOntologyIRI, Boolean ignoreImports) {

        try {

            //check to see if source and target ontologies overlap - source can not have same id as target
            if (sourceOntologies.contains(targetOntologyIRI)) {
                throw new IllegalArgumentException("Source ontology IRIs() overlap with target ontology IRI");
            }

            //load all ontologies
            OntologyIO ioManager = this.loadAllOntologies(ontologyLocations, sourceOntologies, ignoreImports, null);
            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();

            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                //iterate through target classes and extract full mireot
                for (IRI targetClass : targetClasses) {

                    System.out.println("Attempting to extract full mireot for " + targetClass);

                    //get mireot ontology of target
                    mireotManager.getNamedClassParentsToExistingClass(manager, sourceIRI, targetOntologyIRI, targetClass, tempOntology);

                    //add annotations for all named classes in the new module
                    for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                        Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, moduleClass.getIRI());

                        //if annotations have been extracted add them to ontology module
                        if (!tempAnnotations.isEmpty()) {
                            for (OWLAnnotation a : tempAnnotations) {
                                OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                                tempManager.addAxiom(tempOntology, annotationAxiom);
                            }
                        }
                    }//end for

                }
            }

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Extract module from source ontologies based on the targetClasses. Module is built by getting parents
     * (named classes only) up to and including a parent class that exists in the targetOntology - if none of the parent
     * classes are in the targetOntology the parent classes are included up to the root class in the source ontology.
     * The method creates a module which can be merged into the targetOntology.
     * @param targetClasses The set of class IRIs for which the mireot module is required
     * @param manager The manager into which the ontology sources are loaded
     * @param sourceOntologies The IRIs of set of ontologies from which the module should be extracted (i.e. the source ontologies)
     * @param targetOntologyIRI The ID of the ontology from which the target ontology classes are supplied to build the module
     * @return
     */
    public OWLOntology getMireotMerge(Set<IRI> targetClasses, OWLOntologyManager manager, Set<IRI> sourceOntologies, IRI targetOntologyIRI) {

        try {
            //check to see if source and target ontologies overlap - source can not have same id as target
            if (sourceOntologies.contains(targetOntologyIRI)) {
                throw new IllegalArgumentException("Source ontology IRIs() overlap with target ontology IRI");
            }

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();

            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                //iterate through target classes and extract full mireot
                for (IRI targetClass : targetClasses) {

                    System.out.println("Attempting to extract full mireot for " + targetClass);

                    //get mireot ontology of target
                    mireotManager.getNamedClassParentsToExistingClass(manager, sourceIRI, targetOntologyIRI, targetClass, tempOntology);

                    //add annotations for all named classes in the new module
                    for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                        Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, moduleClass.getIRI());

                        //if annotations have been extracted add them to ontology module
                        if (!tempAnnotations.isEmpty()) {
                            for (OWLAnnotation a : tempAnnotations) {
                                OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                                tempManager.addAxiom(tempOntology, annotationAxiom);
                            }
                        }
                    }//end for

                }
            }

            return tempOntology;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }






    public OWLOntology getPartialClosure(Set<IRI> targetClasses, Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports) {

        try {

            //load all ontologies
            OntologyIO ioManager = this.loadAllOntologies(ontologyLocations, sourceOntologies, ignoreImports, null);
            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();


            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                //iterate through target classes and extract partial closure
                for (IRI target : targetClasses) {

                    System.out.println("Attempting to extract partial closure for " + target);

                    //get partial closure of target
                    mireotManager.getPartialClosureForNamedClass(manager, sourceIRI, target, tempOntology);

                    //add annotations for named classes in the new module
                    Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, target);

                    //if annotations have been extracted add them to ontology module
                    if (!tempAnnotations.isEmpty()) {
                        for (OWLAnnotation a : tempAnnotations) {
                            OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(target, a);
                            tempManager.addAxiom(tempOntology, annotationAxiom);
                        }
                    }
                }//end for
            }//end for

            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public OWLOntology getFullClosure(Set<IRI> targetClassIRIs, Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports) {

        try {

            //load all ontologies
            OntologyIO ioManager = this.loadAllOntologies(ontologyLocations, sourceOntologies, ignoreImports, null);
            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();

            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);

            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                for (IRI target : targetClassIRIs) {

                    System.out.println("Attempting to extract full closure for " + target);

                    //get parent classes for target class
                    mireotManager.getNamedClassParentsToRoot(manager, sourceIRI, target, tempOntology);

                    //get partial closure of target class
                    mireotManager.getPartialClosureForNamedClass(manager, sourceIRI, target, tempOntology);

                    //grab the source ontology and the owlclass for the target IRI
                    OWLOntology sourceOntology = manager.getOntology(sourceIRI);
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    OWLClass targetClass = factory.getOWLClass(target);

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

                    for (OWLClass c : namedClassesInAxiom) {
                        System.out.println("Named classes in axioms: " + c.toString());
                        mireotManager.getNamedClassParentsToRoot(manager, sourceIRI, c.getIRI(), tempOntology);

                    }

                    //add annotations for all named classes in the new module
                    for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                        Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, moduleClass.getIRI());
                        //if annotations have been extracted add them to ontology module
                        if (!tempAnnotations.isEmpty()) {
                            for (OWLAnnotation a : tempAnnotations) {
                                OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                                tempManager.addAxiom(tempOntology, annotationAxiom);
                            }

                        }

                    }//end for


                }//end for each target class

                //finally get domain and range classes for object properties if they exist and get their paths to root
                mireotManager.getDomainAndRangeToRoot(manager, sourceIRI, tempOntology);

            }//end for each source ontology
            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param ontologyLocations location of the
     * @param activeOntology    ontology which imports the other ontologies form which the module should be sourced
     * @return
     */
    public OWLOntology getFullClosureImportsAsSource(Set<String> ontologyLocations, IRI activeOntology) {

        try {
            //create ontology io manager
            OntologyIO ioManager = new OntologyIO();
            ioManager = this.loadAllOntologiesNoMerge(ontologyLocations, activeOntology);
            //get the source ontologies from those loaded
            Set<IRI> sourceOntologies = ioManager.getSourceOntologies();
            //target classes are all of those in the activeOntology - get the classes in this signature
            Set<IRI> targetClassIRIs = this.getOntologySignature(ioManager, activeOntology);


            //get handle to ontologies and manager they are loaded in to
            OWLOntologyManager manager = ioManager.getManager();
            //create variables required
            MireotManager mireotManager = new MireotManager();
            //variables for storing ontology module
            // Get hold of an ontology manager
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);

            //iterate through each source ontology
            for (IRI sourceIRI : sourceOntologies) {
                for (IRI target : targetClassIRIs) {

                    System.out.println("Attempting to extract full closure for " + target);

                    //get parent classes for target class
                    mireotManager.getNamedClassParentsToRoot(manager, sourceIRI, target, tempOntology);

                    //get partial closure of target class
                    mireotManager.getPartialClosureForNamedClass(manager, sourceIRI, target, tempOntology);

                    //grab the source ontology and the owlclass for the target IRI
                    OWLOntology sourceOntology = manager.getOntology(sourceIRI);
                    OWLDataFactory factory = manager.getOWLDataFactory();
                    OWLClass targetClass = factory.getOWLClass(target);

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
                    for (OWLClass c : namedClassesInAxiom) {
                        System.out.println("Named classes in axioms: " + c.toString());
                        mireotManager.getNamedClassParentsToRoot(manager, sourceIRI, c.getIRI(), tempOntology);

                    }

                    //add annotations for all named classes in the new module
                    for (OWLClass moduleClass : tempOntology.getClassesInSignature()) {

                        Set<OWLAnnotation> tempAnnotations = mireotManager.getClassAnnotations(manager, sourceIRI, moduleClass.getIRI());
                        //if annotations have been extracted add them to ontology module
                        if (!tempAnnotations.isEmpty()) {
                            for (OWLAnnotation a : tempAnnotations) {
                                OWLAnnotationAxiom annotationAxiom = factory.getOWLAnnotationAssertionAxiom(moduleClass.getIRI(), a);
                                tempManager.addAxiom(tempOntology, annotationAxiom);
                            }

                        }

                    }//end for

                }//end for each target class

                //finally get domain and range classes for object properties if they exist and get their paths to root
                mireotManager.getDomainAndRangeToRoot(manager, sourceIRI, tempOntology);

            }//end for each source ontology
            return tempOntology;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }





    public OntologyIO loadAllOntologies(Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports, IRI targetOntology) {

        //create ontology io manager
        OntologyIO ioManager = new OntologyIO();

        //load all ontologies
        for (String location : ontologyLocations) {
            ioManager.loadOntologyFromFileLocation(location, ignoreImports);
        }
        //set the source and target ontologies
        ioManager.setSourceOntologies(sourceOntologies);
        ioManager.setTargetOntology(targetOntology);


        //get handle to ontologies and manager they are loaded in to
        OWLOntologyManager manager = ioManager.getManager();
        Set<OWLOntology> loadedOntologies = manager.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

        return ioManager;

    }


    public OntologyIO loadAllOntologies(Set<String> ontologyLocations, Set<IRI> sourceOntologies, Boolean ignoreImports) {

        //create ontology io manager
        OntologyIO ioManager = new OntologyIO();

        //load all ontologies
        for (String location : ontologyLocations) {
            ioManager.loadOntologyFromFileLocation(location, ignoreImports);
        }
        //set the source and target ontologies
        ioManager.setSourceOntologies(sourceOntologies);

        //get handle to ontologies and manager they are loaded in to
        OWLOntologyManager manager = ioManager.getManager();
        Set<OWLOntology> loadedOntologies = manager.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

        return ioManager;

    }


    public OntologyIO loadAllOntologiesNoMerge(Set<String> ontologyLocations, IRI targetOntology){

        //create ontology io manager
        OntologyIO ioManager = new OntologyIO();

        //load all ontologies
        for (String location : ontologyLocations) {
            ioManager.loadOntologyFromFileLocationNoMergeImports(location);
        }
        //set the source and target ontologies
        ioManager.setTargetOntology(targetOntology);
        Set<IRI> loadedOntologyIRIs = ioManager.getLoadedOntologyIRIs();
        loadedOntologyIRIs.remove(targetOntology);

        ioManager.setSourceOntologies(loadedOntologyIRIs);


        System.out.println("Target Ontology: " + ioManager.getTargetOntology());
        System.out.println("Source ontologies: " + ioManager.getSourceOntologies());


        return ioManager;
    }






    public Set<IRI> getOntologySignature(OntologyIO ioManager, IRI targetOntologyIRI){

        Set<IRI> targetClassIRIs = new HashSet<IRI>();

        OWLOntologyManager manager = ioManager.getManager();
        OWLOntology targetOntology = manager.getOntology(targetOntologyIRI);

        for(OWLClass cls : targetOntology.getClassesInSignature()){
            targetClassIRIs.add(cls.getIRI());
        }

        return targetClassIRIs;
    }



    public Set<IRI> getOntologySignature(Set<String> ontologyLocations, IRI targetOntologyIRI, Boolean ignoreImports){

        //create variables required
        MireotManager mireotManager = new MireotManager();
        OntologyIO ioManager = new OntologyIO();
        Set<IRI> classesInOntology = new HashSet<IRI>();

        //load all ontologies
        ioManager = this.loadAllOntologiesNoMerge(ontologyLocations, targetOntologyIRI);


        //get handle to ontologies and manager they are loaded in to
        OWLOntologyManager manager = ioManager.getManager();
        Set<OWLOntology> loadedOntologies = manager.getOntologies();
        System.out.println("Loaded ontologies " + loadedOntologies.toString());

        OWLOntology targetOntology = manager.getOntology(targetOntologyIRI);

        for(OWLClass cls : targetOntology.getClassesInSignature()){
            classesInOntology.add(cls.getIRI());
        }

        return classesInOntology;
    }






}