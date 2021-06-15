package org;

import org.apache.commons.cli.*;
import org.gomma.diff.DiffComputation;
import org.gomma.diff.DiffExecutor;
import org.io.OntologyReader;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.webdifftool.client.model.DiffEvolutionMapping;

import java.io.File;

public class ContoDiffMain {

    static Options options;

    static {
        options = new Options();
        Option inputFirstOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_A,
                "first ontology", true, "path of the first ontology");
        Option inputSecondOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_B, "path of the second ontology", true,
                "second ontology");
        Option diff = new Option(ConsoleConstants.DIFF, "compute Diff", false,
                "compute diff");

        options.addOption(inputFirstOnt);
        options.addOption(inputSecondOnt);
        options.addOption(diff);
    }

    public static void main(String[] args) {
        CommandLine cmd = parseCommand(args);
        OntologyReader reader = new OntologyReader();
        OWLOntology firstOnt = null;
        OWLOntology secontOnt = null;
        try {
            System.out.println(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_A));
            System.out.println(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_B));
            File first = new File(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_A));
            File second = new File(cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_B));
            firstOnt = reader.loadOntology(first);
            secontOnt = reader.loadOntology(second);
        } catch (NullPointerException e) {

            System.err.println("first or second ontology not found");
            e.printStackTrace();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("load ontology "+ firstOnt.getClassesInSignature().size());
        if (cmd.hasOption(ConsoleConstants.DIFF)) {
            DiffExecutor.getSingleton().setupRepository();
            DiffComputation computation = new DiffComputation();
            computation.computeDiff(firstOnt, secontOnt);

        }

    }


    private static CommandLine parseCommand(String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }
}
