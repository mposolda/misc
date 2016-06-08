package org.mposolda.jgit;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JgitPlayground {

    public static void main(String[] args) throws Exception {
        Git git = Git.open(new File("/home/mposolda/IdeaProjects/keycloak-examples"));

        Iterable<RevCommit> revisions = git.log().call();
        Iterator<RevCommit> revIterator = revisions.iterator();

        for (int i=0 ; i<5 ; i++) {
            RevCommit commit = revIterator.next();
            System.out.println(commit + " " + new Date((long)commit.getCommitTime() * 1000) + " " + commit.getAuthorIdent().getName());
        }
    }
}
