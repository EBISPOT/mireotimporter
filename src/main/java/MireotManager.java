import com.sun.org.apache.xpath.internal.operations.Bool;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import javax.xml.transform.Source;
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


        for (OWLSubClassOfAxiom ax : sourceOntology
                .getSubClassAxiomsForSubClass(targetClass)) {
            OWLClassExpression superCls = ax.getSuperClass();

            if(!superCls.isAnonymous()) {
                parents.add(superCls.asOWLClass());
            }
        }

        for (OWLClass o : parents){
            System.out.println("named super class " + o.toString());

        }

        return parents;
    }


    public Set<OWLAnnotation> getClassAnnotations(OWLOntologyManager manager, IRI ontologyID, IRI targetClassIRI){


        OWLOntology sourceOntology = manager.getOntology(ontologyID);
        Set<OWLClass> parents = new HashSet<OWLClass>();

        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLClass targetClass = factory.getOWLClass(targetClassIRI);

        Set<OWLAnnotation> targetClassAnnotations = targetClass.getAnnotations(sourceOntology);

        /*for (OWLAnnotation o : targetClassAnnotations){
           System.out.println("annotation: " + o.toString());

        }
        */
        return targetClassAnnotations;
    }


    public OWLOntology getNamedClassParentsToRoot(OWLOntologyManager manager, IRI ontologyID, IRI targetClassIRI){

        try {
            //create variables for storing the ontology of parents to root with subclass axioms
            OWLOntologyManager tempManager = OWLManager.createOWLOntologyManager();
            IRI iriTemp = IRI.create("http://mireotmodule.owl");
            OWLOntology tempOntology = tempManager.createOntology(iriTemp);
            OWLDataFactory factory = tempManager.getOWLDataFactory();

            //OWLOntology sourceOntology = manager.getOntology(ontologyID);
            //Set<OWLClass> parents = new HashSet<OWLClass>();

            //OWLDataFactory factory = manager.getOWLDataFactory();
            OWLClass targetClass = factory.getOWLClass(targetClassIRI);

            //get first set of parents
            Set<OWLClass> nextParents = getNamedClassParents(manager, ontologyID, targetClassIRI);

            //add first parents to super set of all parents
            //parents.addAll(nextParents);

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

                    System.out.println("size next parents " + nextParents.size());
                    System.out.println("next parent " + c.getIRI());

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
                //stop if there are no parents - we've reached top
                if (tempParents.isEmpty()) {
                    finished = true;
                } else {
                    finished = false;
                   // parents.addAll(tempParents);
                    nextParents.clear();
                    nextParents.addAll(tempParents);
                }
            }

            return tempOntology;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public void getNamedClassAncestors(){

    }

    public void getSubClassOf(){

    }

    public void getNamedClassInAxiom(){

    }


}
