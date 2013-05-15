/*******************************************************************************
 * Copyright (C) 2013 Research In Motion Limited
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
 
package com.bb.extensions.plugin.unittests.internal.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * @author nbilal
 * 
 */
public class GitWrapper {
	/**
	 * The JGit object
	 */
	private Git git = null;

	/**
	 */
	private GitWrapper() {

	}

	/**
	 * Opens an existing git repository located in the file system at the given
	 * path (e.g. C:/MyGitRepo).
	 * 
	 * @param path
	 * @return The GitWrapper object to interact with the opened git repository.
	 */
	public static GitWrapper openRepository(String path) {
		GitWrapper gitWrapper = new GitWrapper();
		File repoDir = new File(path + "/.git");
		if (repoDir.exists()) {
			try {
				gitWrapper.git = Git.open(repoDir);
				return gitWrapper;
			} catch (NoWorkTreeException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Close the repository
	 */
	public void closeRepository() {
		Repository repo = this.git.getRepository();
		if (repo != null) {
			repo.close();
		}
	}

	/**
	 * Clones the git repository located at the given url to the given path.
	 * 
	 * @param path
	 * 
	 * @param url
	 * @return The GitWrapper to interact with the clone git repository.
	 * @throws IllegalArgumentException
	 */
	public static GitWrapper cloneRepository(String path, String url)
			throws IllegalArgumentException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File gitDir = new File(path);
		if (gitDir.exists() == false) {
			gitDir.mkdir();
		} else if (gitDir.list().length > 0) {
			if (isGitRepository(gitDir)) {
				return openRepository(path);
			} else {
				throw new IllegalArgumentException(
						"Cannot clone to a non-empty directory");
			}
		}
		Repository repository;
		try {
			repository = builder.setGitDir(gitDir).readEnvironment()
					.findGitDir().build();
			GitWrapper gitWrapper = new GitWrapper();
			gitWrapper.git = new Git(repository);
			CloneCommand clone = Git.cloneRepository();
			clone.setBare(false);
			clone.setCloneAllBranches(true);
			clone.setDirectory(gitDir).setURI(url);
			// we have to close the newly returned Git object as call() creates
			// a new one every time
			clone.call().getRepository().close();
			return gitWrapper;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param dir
	 * @return true if dir contains a .git file
	 */
	private static boolean isGitRepository(File dir) {
		if (dir.list().length > 0) {
			for (String fileName : dir.list()) {
				if (fileName.equals(".git")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @return true if the git repo is up to date.
	 */
	public boolean isRepoUpToDate() {
		boolean upToDate = true;
		FetchCommand fetchCmd = this.git.fetch();
		try {
			fetchCmd.call();
			BranchTrackingStatus status = BranchTrackingStatus.of(this.git
					.getRepository(), this.git.getRepository().getBranch());
			upToDate = status.getBehindCount() == 0;
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return upToDate;
	}

	/**
	 * @return performs a pull from origin
	 */
	public boolean pullMocks() {
		PullCommand pullCmd = git.pull();
		try {
			PullResult pullResult = pullCmd.call();
			return pullResult.isSuccessful();
		} catch (WrongRepositoryStateException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		} catch (DetachedHeadException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (CanceledException e) {
			e.printStackTrace();
		} catch (RefNotFoundException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @return true if the git repo is clean according to "git status".
	 */
	public boolean isRepoClean() {
		try {
			org.eclipse.jgit.api.Status status = git.status().call();
			return status.isClean();
		} catch (NoWorkTreeException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		return false;
	}
}