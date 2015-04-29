import com.sun.org.apache.xpath.internal.operations.Bool;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import javax.xml.transform.Source;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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



    public OWLOntology getNamedClassParentsToExistingClass(OWLOntologyManager manager, IRI sourceOntologyID, IRI targetOntologyIRI, IRI targetClassIRI, OWLOntology tempOntology) {

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
                Set<OWLClass> nextParents = getNamedClassParents(manager, sourceOntologyID, targetClassIRI);

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
                    //if there are classes already in existance in target, remove form the mireot module
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

                            Set<OWLClass> tempSet = getNamedClassParents(manager, sourceOntologyID, c.getIRI());

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
                            //stop if there are no parents - we've reached top
                            if (tempParents.isEmpty()) {
                                finished = true;
                            } else {

                                finished = false;
                                nextParents.clear();
                                nextParents.addAll(tempParents);
                            }
                        }//end for

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



    public void getNamedClassInAxiom(){

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
        }

        return tempOntology;

    }



    public Set<OWLClassAxiom> getNamedClassAxioms(OWLOntologyManager manager, IRI sourceOntologyIRI, IRI targetClassIRI){

        OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);
        Set<OWLClassAxiom> targetAxioms = new HashSet<OWLClassAxiom>();

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);

        targetAxioms = sourceOntology.getAxioms(targetClass);

        for(OWLClassAxiom a : targetAxioms){
            System.out.println("axiom on target: " + a.toString());
        }

        return targetAxioms;
    }



    public void getFullClosureForNamedClass(OWLOntologyManager manager, IRI sourceOntologyIRI, IRI targetClassIRI, OWLOntology tempOntology){

        try {
            //create variables for storing the ontology of parents to root with subclass axioms
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = tempManager.getOWLDataFactory();
            OWLClass targetClass = factory.getOWLClass(targetClassIRI);
            OWLOntology sourceOntology = manager.getOntology(sourceOntologyIRI);

            //get parents to root
            Set<OWLClass> nextParents = getNamedClassParents(manager, sourceOntologyIRI, targetClassIRI);




        }
        catch(Exception e){
            e.printStackTrace();
        }


    }



}
