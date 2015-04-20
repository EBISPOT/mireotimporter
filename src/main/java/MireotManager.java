import org.semanticweb.owlapi.model.*;

/**
 * Created by malone on 27/03/2015.
 */
public class MireotManager {

    /**
     * 
     * @param manager
     * @param ontologyID
     * @param targetClass
     * @return
     */
    public OWLClass getNamedClassParent(OWLOntologyManager manager, IRI ontologyID, OWLClass targetClass){

        OWLOntology sourceOntology = manager.getOntology(ontologyID);


        for (OWLSubClassOfAxiom ax : sourceOntology
                .getSubClassAxiomsForSubClass(targetClass)) {
            OWLClassExpression superCls = ax.getSuperClass();

            if(!superCls.isAnonymous()) {

                System.out.println("named super class " + superCls.toString());
            }
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
