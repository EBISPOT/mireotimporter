import org.semanticweb.owlapi.model.*;

/**
 * Created by malone on 27/03/2015.
 */
public class MireotManager {


    public OWLClass getNamedClassParent(OWLOntologyManager manager, IRI ontologyID, OWLClass targetClass){

        OWLOntology sourceOntology = manager.getOntology(ontologyID);


        for (OWLSubClassOfAxiom ax : sourceOntology
                .getSubClassAxiomsForSubClass(targetClass)) {
            OWLClassExpression superCls = ax.getSuperClass();
            System.out.println("superCls");
        }


        //replace
        return null;
    }


    public void getClassAnnotations(){

    }


    public void getNamedClassAncestors(){

    }

    public void getSubClassOf(){

    }

    public void getNamedClassInAxiom(){

    }


}
