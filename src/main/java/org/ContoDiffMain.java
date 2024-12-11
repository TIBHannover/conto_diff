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

package org;


import org.apache.commons.cli.*;
import org.gomma.diff.DiffComputation;
import org.gomma.diff.DiffExecutor;
import org.io.OntologyReader;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.webdifftool.client.model.DiffEvolutionMapping;
import org.webdifftool.client.model.GitInfoParams;
import org.webdifftool.client.model.SemanticDiff;
import org.webdifftool.client.model.changes.Change;
import org.webdifftool.server.OWLManagerCustom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContoDiffMain {

    static Options options;

    static {
        options = new Options();
        Option inputFirstOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_A, "first ontology", true, "path of the first ontology");

        Option inputFirstOntIri = new Option("iria", ConsoleConstants.INPUT_ONTOLOGY_A_IRI, true, "IRI of the first ontology");

        Option inputSecondOnt = new Option(ConsoleConstants.INPUT_ONTOLOGY_B, "path of the second ontology", true, "second ontology");

        Option inputSecondOntIri = new Option("irib", ConsoleConstants.INPUT_ONTOLOGY_B_IRI, true, "IRI of the second ontology");

        Option baseOntIri = new Option("base", ConsoleConstants.BASE_ONTOLOGY_IRI, true, "Base Ontology IRI from which semantic diffs are calculated");

        Option outputFile =  new Option(ConsoleConstants.OUTPUT_FILE, "file with the compact diff representation", true,
                "output file");
        Option diff = new Option(ConsoleConstants.DIFF, "compute Diff", false,
                "compute diff");
        Option gitInfo = Option.builder(ConsoleConstants.GIT_INFO)
                        .hasArgs()
                        .valueSeparator('\u001f')
                        .argName("PARAMS")
                        .desc("Params for the git info")
                        .build();

        options.addOption(inputFirstOnt);
        options.addOption(inputFirstOntIri);
        options.addOption(inputSecondOnt);
        options.addOption(inputSecondOntIri);
        options.addOption(baseOntIri);
        options.addOption(outputFile);
        options.addOption(diff);
        options.addOption(gitInfo);
    }

    public static void main(String[] args) throws IOException {
        CommandLine cmd = parseCommand(args);
        OntologyReader reader = new OntologyReader();
        OWLOntology firstOnt = null;
        OWLOntology secondOnt = null;
        FileWriter output = null;
        String firstIri = "", secondIri = "", ontologyIri = "";
        GitInfoParams gitInfoParams = null;
        try {

            String[] values = cmd.getOptionValues(ConsoleConstants.GIT_INFO);
            gitInfoParams = new GitInfoParams(values[0], values[1],
                    values[2], values[3],
                    values[4], values[5],
                    values[6], values[7]);

            String first = cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_A);
            String second = cmd.getOptionValue(ConsoleConstants.INPUT_ONTOLOGY_B);
            firstIri = gitInfoParams.getRawUrlLeft();

            secondIri = gitInfoParams.getRawUrlRight();

            ontologyIri = cmd.getOptionValue(ConsoleConstants.BASE_ONTOLOGY_IRI);

            firstOnt = setOntology(reader, first, null);
            secondOnt = setOntology(reader, second, null);
            output = new FileWriter(cmd.getOptionValue(ConsoleConstants.OUTPUT_FILE));
        } catch (OWLOntologyCreationException e) {
            System.exit(10);
        }

            DiffExecutor.getSingleton().setupRepository();
            DiffComputation computation = new DiffComputation();
            DiffEvolutionMapping mapping = computation.computeDiff(firstOnt, secondOnt);
            Map<String, String> prefixes = OWLManagerCustom.getAllPrefixes(firstOnt, secondOnt);

            SemanticDiff sdiff = new SemanticDiff();

            for (Map.Entry<String, Change> change : mapping.allChanges.entrySet()) {
                OWLManagerCustom.setProvDmMap(change.getKey(), change.getValue().getSimpleWordRepresentation().trim(), sdiff.getProvDMs());
            }

            OWLManagerCustom.generateLocationMap(sdiff.getProvDMs(), sdiff.getLocations());


            sdiff.setBaseEntity(OWLManagerCustom.generateBaseEntity(ontologyIri));

            String secondSha1 = getSha1FromIri(gitInfoParams.getRightCommitUri());

            OWLManagerCustom.generateActivityMap(sdiff.getLocations(), sdiff.getActivities(), secondSha1);

            String[] URIs = new String[]{gitInfoParams.getRawUrlLeft(), gitInfoParams.getRawUrlRight()};
            for (String rawUri : URIs) {
                OWLManagerCustom.generateSourceEntiry(rawUri, sdiff.getSourceEntities());
            }

            sdiff.setSoftwareAgent(OWLManagerCustom.generateSoftwareAgent());

            String firstSha1 = getSha1FromIri(gitInfoParams.getLeftCommitUri());

            OWLManagerCustom.generateSDiffEntity(sdiff.getSourceEntities(), firstSha1, secondSha1, sdiff.getSdiffEntities(), sdiff.getBaseEntity(), gitInfoParams);

            String filePath = "semantic_diff.json";

            OWLManagerCustom.writeSemanticDiffToFile(sdiff, filePath);
            StringBuilder sb = new StringBuilder();

            sb.append(OWLManagerCustom.prefixesMapToString(prefixes)).append(System.lineSeparator());
            sb.append(OWLManagerCustom.getAutoGeneratedHeader(firstOnt));
            sb.append(sdiff.getBaseEntity()).append(System.lineSeparator()).append(System.lineSeparator());

            for (Map.Entry<String, String> entry : sdiff.getLocations().entrySet()) {
                sb.append(entry.getValue());
            }

            for (Map.Entry<String, String> entry : sdiff.getActivities().entrySet()) {
                sb.append(entry.getValue());
            }

            for (Map.Entry<String, String> entry : sdiff.getSourceEntities().entrySet()) {
                sb.append(entry.getValue());
            }

            for (Map.Entry<String, String> entry : sdiff.getSdiffEntities().entrySet()) {
                sb.append(entry.getValue()).append(System.lineSeparator());
            }
            sb.append(sdiff.getSoftwareAgent());

            if (output != null){
                output.write(sb.toString());
            }
            StringBuilder diffs = new StringBuilder();
            for (String s : sdiff.getProvDMs().values()) {
                diffs.append(s);
            }
            Files.write(Paths.get("all_diffs.nq"), diffs.toString().getBytes(StandardCharsets.UTF_8));
            output.close();


    }


    public static OWLOntology setOntology(OntologyReader reader, String file, String iri) throws IllegalArgumentException, OWLOntologyCreationException {
        if (file != null && iri != null) {
            throw new IllegalArgumentException("File and IRI are provided at the same time!");
        }
        else if (file != null) {
            return reader.loadOntology(new File(file));
        }
        else if (iri != null) {
            return reader.loadOntology(IRI.create(iri));
        }
        else return null;
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

    public static String getSha1FromIri(String iri) {
        String[] parts = iri.split("/");
        String firstSha1 = "";
        Pattern pattern = Pattern.compile("[0-9a-f]{40}");
        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.matches()) {
                firstSha1 = part;
                break;
            }
        }
        return firstSha1;
    }
}
