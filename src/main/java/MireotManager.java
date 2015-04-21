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

        for (OWLAnnotation o : targetClassAnnotations){
            System.out.println("annotation: " + o.toString());

        }

        return targetClassAnnotations;

    }


    public void getNamedClassAncestors(){

    }

    public void getSubClassOf(){

    }

    public void getNamedClassInAxiom(){

    }


}
