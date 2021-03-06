import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by malone on 27/03/2015.
 */
public class MireotManager {

    /**
     * 
     * @param manager
     * @param ontologyID
     * @param targetClassIRI
     * @return
     */
    public Set<OWLClass> getNamedClassParents(OWLOntologyManager manager, IRI ontologyID, IRI targetClassIRI){

        OWLOntology sourceOntology = manager.getOntology(ontologyID);
        Set<OWLClass> parents = new HashSet<OWLClass>();

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);

        if(sourceOntology.containsClassInSignature(targetClassIRI)) {
            for (OWLSubClassOfAxiom ax : sourceOntology.getSubClassAxiomsForSubClass(targetClass)) {
                OWLClassExpression superCls = ax.getSuperClass();

                if (!superCls.isAnonymous()) {
                    parents.add(superCls.asOWLClass());
                }
            }
        }
        else{
            System.out.println("Could not find class " + targetClassIRI + " in ontology " + ontologyID.toString());
        }

        return parents;
    }


    public Set<OWLAnnotation> getClassAnnotations(OWLOntologyManager manager, IRI ontologyID, IRI targetClassIRI){

        //get source ontology and handle to the class in question
        OWLOntology sourceOntology = manager.getOntology(ontologyID);
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);

        //get all the annotations for the target class
        Set<OWLAnnotation> targetClassAnnotations = targetClass.getAnnotations(sourceOntology);

        return targetClassAnnotations;
    }


    public OWLOntology getNamedClassParentsToRoot(OWLOntologyManager manager, IRI ontologyID, IRI targetClassIRI, OWLOntology tempOntology){

        try {

            //create variables for storing the ontology of parents to root with subclass axioms
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = tempManager.getOWLDataFactory();
            OWLClass targetClass = factory.getOWLClass(targetClassIRI);

            //get first set of parents
            Set<OWLClass> nextParents = getNamedClassParents(manager, ontologyID, targetClassIRI);

            //set flag to enter loop if there are parents
            Boolean finished;
            if (nextParents.isEmpty()) {
                finished = true;
            }
            //otherwise there are parents, continue to traverse
            else {
                finished = false;
                //add these first parents and the initial class
                for (OWLClass newParent : nextParents) {

                    OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(newParent);
                    OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetClass, newParent);
                    tempManager.addAxiom(tempOntology, namedParentAxiom);
                    tempManager.addAxiom(tempOntology, subclassAxiom);
                }
            }

            while (!finished) {
                //store next set of parents in temp set
                Set<OWLClass> tempParents = new HashSet<OWLClass>();
                //for each parent
                for (OWLClass c : nextParents) {

                    Set<OWLClass> tempSet = getNamedClassParents(manager, ontologyID, c.getIRI());

                    if (!tempSet.isEmpty()) {

                        //make current class a subclass of parents
                        //add named classes to ontology
                        for (OWLClass newParent : tempSet) {

                            OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(newParent);
                            OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(c, newParent);
                            tempManager.addAxiom(tempOntology, namedParentAxiom);
                            tempManager.addAxiom(tempOntology, subclassAxiom);
                        }

                        //add to set
                        tempParents.addAll(tempSet);
                    }

                }
                //stop if there are no parents - we've reached root
                if (tempParents.isEmpty()) {
                    finished = true;
                } else {
                    finished = false;
                    nextParents.clear();
                    nextParents.addAll(tempParents);
                }
            }//end while

            return tempOntology;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }



    public OWLOntology getNamedClassParentsToExistingClass(OWLOntologyManager manager, IRI sourceOntologyIRI, IRI targetOntologyIRI, IRI targetClassIRI, OWLOntology tempOntology) {

        try {

            //create variables for storing the ontology of parents to root with subclass axioms
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = tempManager.getOWLDataFactory();
            OWLClass targetClass = factory.getOWLClass(targetClassIRI);
            OWLOntology targetOntology = manager.getOntology(targetOntologyIRI);

            //first check to see if the target class is in the target ontology
            Boolean containsTarget = targetOntology.containsClassInSignature(targetClassIRI);

            //if the target class is not in the ontology then continue
            if(!containsTarget) {
                //get first set of parents
                Set<OWLClass> nextParents = getNamedClassParents(manager, sourceOntologyIRI, targetClassIRI);

                //set flag to enter loop if there are parents
                Boolean finished;
                if (nextParents.isEmpty()) {
                    finished = true;
                }
                //otherwise there are parents, continue to traverse
                else {
                    finished = false;
                    Set<OWLClass> removeList = new HashSet<OWLClass>();
                    //check to see if the parents are in the active ontology already
                    //otherwise add these first parents and the initial class
                    for (OWLClass newParent : nextParents) {

                        //if the class is not already in the ontology
                        System.out.println("Parent is " + newParent.getIRI());

                        if (!targetOntology.containsClassInSignature(newParent.getIRI())) {
                            OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(newParent);
                            OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetClass, newParent);
                            tempManager.addAxiom(tempOntology, namedParentAxiom);
                            tempManager.addAxiom(tempOntology, subclassAxiom);
                        }
                        //make note to remove the class if it is in the ontology already
                        else {
                            System.out.println("Parent is in target ontology already: " + newParent.getIRI());
                            OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(newParent);
                            OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(targetClass, newParent);
                            tempManager.addAxiom(tempOntology, namedParentAxiom);
                            tempManager.addAxiom(tempOntology, subclassAxiom);
                            removeList.add(newParent);
                        }
                    }
                    //if there are classes already in existence in target, remove form the mireot module
                    if (!removeList.isEmpty()) {
                        nextParents.removeAll(removeList);
                    }
                }

                if (!nextParents.isEmpty()) {
                    while (!finished) {
                        //store next set of parents in temp set which we will use to store for output
                        Set<OWLClass> tempParents = new HashSet<OWLClass>();

                        //remove list
                        Set<OWLClass> removeList = new HashSet<OWLClass>();

                        //for each parent
                        for (OWLClass c : nextParents) {
                            System.out.println("parent " + c.getIRI());

                            Set<OWLClass> tempSet = getNamedClassParents(manager, sourceOntologyIRI, c.getIRI());

                            if (!tempSet.isEmpty()) {

                                //make current class a subclass of parents
                                //add named classes to ontology
                                for (OWLClass newParent : tempSet) {

                                    OWLDeclarationAxiom namedParentAxiom = factory.getOWLDeclarationAxiom(newParent);
                                    OWLAxiom subclassAxiom = factory.getOWLSubClassOfAxiom(c, newParent);
                                    tempManager.addAxiom(tempOntology, namedParentAxiom);
                                    tempManager.addAxiom(tempOntology, subclassAxiom);

                                    //remove the class if it is in the ontology
                                    if (targetOntology.containsClassInSignature(newParent.getIRI())) {
                                        System.out.println("Exists in target ontology " + newParent.getIRI());
                                        removeList.add(newParent);
                                    }

                                    //add to set
                                    tempParents.addAll(tempSet);
                                    if (!removeList.isEmpty()) {
                                        tempParents.removeAll(removeList);
                                    }
                                }
                            }
                        }//end for

                        //stop if there are no parents - we've reached top
                        if (tempParents.isEmpty()) {
                            finished = true;
                        } else {

                            finished = false;
                            nextParents.clear();
                            nextParents.addAll(tempParents);
                        }
                    }//end while
                }
                return tempOntology;
            }
            //the target class is in the ontology already
            else{
                System.out.println("Class "+targetClassIRI+ " already in target ontology");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
                return null;
    }



    public OWLOntology getPartialClosureForNamedClass(OWLOntologyManager manager, IRI sourceOntologyIRI, IRI targetClassIRI, OWLOntology tempOntology) {

        //create variables for storing the ontology of parents to root with subclass axioms
        OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = tempManager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);
        OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);

        //get named class axioms
        Set<OWLClassAxiom> namedClassAxioms = getNamedClassAxioms(manager, sourceOntologyIRI, targetClassIRI);

        //add axioms to tempOntology
        for(OWLClassAxiom a : namedClassAxioms){
            tempManager.addAxiom(tempOntology, a);

            //check to see if property used in axioms is in the ontology already
            //get object properties used in the axiom
            Set<OWLObjectProperty> objectProperties = a.getObjectPropertiesInSignature();
            for(OWLObjectProperty property : objectProperties) {
                //if the module ontology does not contain the object property already
                if(!tempOntology.containsClassInSignature(property.getIRI())) {
                    OWLDataFactory sourceFactory = manager.getOWLDataFactory();
                    OWLObjectProperty propertyToAdd = sourceFactory.getOWLObjectProperty(property.getIRI());

                    //add as subproperty of top object property and then add to module ontology
                    OWLObjectProperty topProperty = factory.getOWLObjectProperty(IRI.create("http://www.w3.org/2002/07/owl#topObjectProperty"));
                    OWLAxiom owlSubObjectPropertyOfAxiom = factory.getOWLSubObjectPropertyOfAxiom(propertyToAdd, topProperty);
                    tempManager.addAxiom(tempOntology, owlSubObjectPropertyOfAxiom);

                    //get characteristics for object properties
                    Set<OWLAxiom> propertyAxioms = new HashSet<OWLAxiom>();
                    propertyAxioms = this.getObjectPropertyCharacteristics(sourceOntology, sourceFactory, property, propertyToAdd);

                    //add all axioms about property
                    tempManager.addAxioms(tempOntology, propertyAxioms);

                    //get any disjoints
                    Set<OWLObjectPropertyExpression> disjoints = property.getDisjointProperties(sourceOntology);

                    //now call to get properties on these disjoints if they aren't in the ontology already


                }
            }

        }

        return tempOntology;
    }


    /**
     *
     * @param manager
     * @param sourceOntologyIRI
     * @param tempOntology
     * @return
     */
    public OWLOntology getDomainAndRangeToRoot(OWLOntologyManager manager, IRI sourceOntologyIRI, OWLOntology tempOntology){


        System.out.println("attempting to get domain and range roots");

        //var to store axioms about properties
        Set<OWLAxiom> propertyAxioms = new HashSet<OWLAxiom>();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);

        //get all object properties in the ontology and iterate through each one
        Set<OWLObjectProperty> properties = tempOntology.getObjectPropertiesInSignature();

        for(OWLObjectProperty property : properties){

            //process any domains and ranges and add to module ontology
            Set<OWLClassExpression> propertyDomains = property.getDomains(tempOntology);
            Set<OWLClassExpression> propertyRanges = property.getRanges(tempOntology);

            System.out.println("property " + property.toString());

            if(!propertyDomains.isEmpty()) {
                for(OWLClassExpression e : propertyDomains){
                    System.out.println("domain " + e.toString());
                    this.getNamedClassParentsToRoot(manager, sourceOntologyIRI, e.asOWLClass().getIRI(), tempOntology);

                }
            }
            if(!propertyRanges.isEmpty()) {
                for(OWLClassExpression e : propertyRanges){
                    System.out.println("range " + e.toString());
                    getNamedClassParentsToRoot(manager, sourceOntologyIRI, e.asOWLClass().getIRI(), tempOntology);
                }
            }

        }

        return tempOntology;
    }


    /**
     * given an ontology and an object property obtain all of the characteristics as a set of OWLAxioms about
     * that object property
     * @return
     */
    public Set<OWLAxiom> getObjectPropertyCharacteristics(OWLOntology sourceOntology, OWLDataFactory sourceFactory, OWLObjectProperty property, OWLObjectProperty propertyToAdd){

        //var to store axioms
        Set<OWLAxiom> propertyAxioms = new HashSet<OWLAxiom>();

        //process any domains and ranges and add to module ontology
        Set<OWLClassExpression> propertyDomains = property.getDomains(sourceOntology);
        Set<OWLClassExpression> propertyRanges = property.getRanges(sourceOntology);

        if(!propertyDomains.isEmpty()) {
            for(OWLClassExpression e : propertyDomains){
                propertyAxioms.add(sourceFactory.getOWLObjectPropertyDomainAxiom(propertyToAdd,e.asOWLClass()));
            }
        }
        if(!propertyRanges.isEmpty()) {
            for(OWLClassExpression e : propertyRanges){
                propertyAxioms.add(sourceFactory.getOWLObjectPropertyRangeAxiom(propertyToAdd, e.asOWLClass()));
            }
        }

        //get any characteristics
        Boolean isFunctional = property.isFunctional(sourceOntology);
        if(isFunctional){
            propertyAxioms.add(sourceFactory.getOWLFunctionalObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isTransitive = property.isTransitive(sourceOntology);
        if(isTransitive){
            propertyAxioms.add(sourceFactory.getOWLTransitiveObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isAsymmetric = property.isAsymmetric(sourceOntology);
        if(isAsymmetric){
            propertyAxioms.add(sourceFactory.getOWLAsymmetricObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isInverseFunctional = property.isInverseFunctional(sourceOntology);
        if(isInverseFunctional){
            propertyAxioms.add(sourceFactory.getOWLInverseFunctionalObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isIrreflexive = property.isIrreflexive(sourceOntology);
        if(isIrreflexive){
            propertyAxioms.add(sourceFactory.getOWLIrreflexiveObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isReflexive = property.isReflexive(sourceOntology);
        if(isReflexive){
            propertyAxioms.add(sourceFactory.getOWLReflexiveObjectPropertyAxiom(propertyToAdd));
        }
        Boolean isSymmetric = property.isSymmetric(sourceOntology);
        if(isSymmetric){
            propertyAxioms.add(sourceFactory.getOWLSymmetricObjectPropertyAxiom(propertyToAdd));
        }

        //get annotation properties on object properties
        Set<OWLAnnotation> propertyAnnotations = property.getAnnotations(sourceOntology);
        for(OWLAnnotation annot : propertyAnnotations){
            propertyAxioms.add(sourceFactory.getOWLAnnotationAssertionAxiom(property.getIRI(), annot));
        }

    return propertyAxioms;

    }



    public Set<OWLClassAxiom> getNamedClassAxioms(OWLOntologyManager manager, IRI sourceOntologyIRI, IRI targetClassIRI){

        OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);
        Set<OWLClassAxiom> targetAxioms = new HashSet<OWLClassAxiom>();

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);

        targetAxioms = sourceOntology.getAxioms(targetClass);

        return targetAxioms;
    }






}
