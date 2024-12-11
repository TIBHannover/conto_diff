/*
 *
 *  * Copyright © 2014 - 2021 Leipzig University (Database Research Group)
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, version 3.
 *  *
 *  * This program is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.io;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.model.*;

import java.io.File;


public class OntologyReader {

    OWLOntologyManager manager;


    public OntologyReader(){


    }

    public OWLOntology loadOntology(File f) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        OWLOntologyLoaderConfiguration loaderConfig = new OWLOntologyLoaderConfiguration()
                .setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        FileDocumentSource source = new FileDocumentSource(f);
        return manager.loadOntologyFromOntologyDocument(source, loaderConfig);
    }

//    public OWLOntology loadOntology(File f) throws OWLOntologyCreationException {
//        manager = OWLManager.createOWLOntologyManager();
//        return manager.loadOntologyFromOntologyDocument(f);
//    }

    public OWLOntology loadOntology(IRI iri) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        OWLOntologyLoaderConfiguration loaderConfig = new OWLOntologyLoaderConfiguration()
                .setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
        IRIDocumentSource source = new IRIDocumentSource(iri);
        return manager.loadOntologyFromOntologyDocument(source, loaderConfig);
    }

//    public OWLOntology loadOntology(IRI iri) throws OWLOntologyCreationException {
//        manager = OWLManager.createOWLOntologyManager();
//        return manager.loadOntology(iri);
//    }
}
