/*
 * Copyright (C)2014, Novartis Institutes for BioMedical Research Inc.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 * 
 * - Neither the name of Novartis Institutes for BioMedical Research Inc.
 *   nor the names of its contributors may be used to endorse or promote
 *   products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.rdkit.lucene.fingerprint;

import org.RDKit.ExplicitBitVect;
import org.RDKit.RDKFuncs;
import org.RDKit.ROMol;
import org.RDKit.UInt_Vect;
import org.rdkit.lucene.util.CommonUtils;

/** Defines supported fingerprint types. */
public enum FingerprintType {

	morgan("Morgan") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					iRadius,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getRadius() <= 0) {
				throw new InvalidFingerprintSettingsException("Radius must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			return RDKFuncs.getMorganFingerprintAsBitVect(mol, settings.getRadius(), settings.getNumBits());
		}
	},

	featmorgan("FeatMorgan") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					iRadius,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getRadius() <= 0) {
				throw new InvalidFingerprintSettingsException("Radius must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			final UInt_Vect ivs= new UInt_Vect(mol.getNumAtoms());

			try {
				RDKFuncs.getFeatureInvariants(mol, ivs);
				return RDKFuncs.getMorganFingerprintAsBitVect(mol, settings.getRadius(), settings.getNumBits(), ivs);
			}
			finally {
				ivs.delete();
			}
		}
	},

	atompair("AtomPair") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iAtomPairMinPath,
					iAtomPairMaxPath,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getAtomPairMinPath() != FingerprintSettings.UNAVAILABLE && settings.getAtomPairMinPath() <= 0) {
				throw new InvalidFingerprintSettingsException("AtomPair minimal path must be a positive number > 0.");
			}
			if (settings.getAtomPairMaxPath() != FingerprintSettings.UNAVAILABLE && settings.getAtomPairMaxPath() <= 0) {
				throw new InvalidFingerprintSettingsException("AtomPair maximal path must be a positive number > 0.");
			}
			if (settings.getAtomPairMaxPath() < settings.getAtomPairMinPath()) {
				throw new InvalidFingerprintSettingsException("AtomPair maximal path must be greater than or equal to AtomPair minimal path.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			int iAtomPairMinPath = settings.getAtomPairMinPath();
			int iAtomPairMaxPath = settings.getAtomPairMaxPath();

			// Use old default values, if the value is undefined
			if (!settings.isAvailable(iAtomPairMinPath)) {
				iAtomPairMinPath = 1;
			}
			if (!settings.isAvailable(iAtomPairMaxPath)) {
				iAtomPairMaxPath = ((1 << 5) - 1) - 1;
			}

			return RDKFuncs.getHashedAtomPairFingerprintAsBitVect(mol, settings.getNumBits(),
					iAtomPairMinPath, iAtomPairMaxPath);
		}
	},

	torsion("Torsion") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					iTorsionPathLength,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getTorsionPathLength() != FingerprintSettings.UNAVAILABLE && settings.getTorsionPathLength() <= 0) {
				throw new InvalidFingerprintSettingsException("Torsion path length must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			int iTorsionPathLength = settings.getTorsionPathLength();

			// Use old default value, if the value is undefined
			if (!settings.isAvailable(iTorsionPathLength)) {
				iTorsionPathLength = 4;
			}

			return RDKFuncs.getHashedTopologicalTorsionFingerprintAsBitVect(mol, settings.getNumBits(), iTorsionPathLength);
		}
	},

	rdkit("RDKit") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					iMinPath,
					iMaxPath,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getMinPath() <= 0) {
				throw new InvalidFingerprintSettingsException("Minimal path must be a positive number > 0.");
			}
			if (settings.getMaxPath() <= 0) {
				throw new InvalidFingerprintSettingsException("Maximal path must be a positive number > 0.");
			}
			if (settings.getMaxPath() < settings.getMinPath()) {
				throw new InvalidFingerprintSettingsException("Maximal path must be greater than or equal to minimal path.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			return RDKFuncs.RDKFingerprintMol(
					mol, settings.getMinPath(), settings.getMaxPath(), settings.getNumBits(), 2);
		}
	},

	avalon("Avalon") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iAvalonQueryFlag,
					iAvalonBitFlags);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			final int bitNumber = settings.getNumBits();
			final ExplicitBitVect fingerprint = new ExplicitBitVect(bitNumber);
			synchronized (AVALON_FP_LOCK) {
				RDKFuncs.getAvalonFP(mol, fingerprint, bitNumber, settings.getAvalonQueryFlag() == 1, false /** reset Vector */,
						settings.getAvalonBitFlags());
			}
			return fingerprint;
		}
	},

	layered("Layered") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					iMinPath,
					iMaxPath,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					iLayerFlags,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
			if (settings.getMinPath() <= 0) {
				throw new InvalidFingerprintSettingsException("Minimal path must be a positive number > 0.");
			}
			if (settings.getMaxPath() <= 0) {
				throw new InvalidFingerprintSettingsException("Maximal path must be a positive number > 0.");
			}
			if (settings.getMaxPath() < settings.getMinPath()) {
				throw new InvalidFingerprintSettingsException("Maximal path must be greater than or equal to minimal path.");
			}
			if (settings.getLayerFlags() <= 0) {
				throw new InvalidFingerprintSettingsException("Layer flags must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			return RDKFuncs.LayeredFingerprintMol(mol, settings.getLayerFlags(), settings.getMinPath(),
					settings.getMaxPath(), settings.getNumBits());
		}

	},

	maccs("MACCS") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					166,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
		}

		/**
		 * Calculates the fingerprint for the specified molecule based on the
		 * specified settings.
		 * 
		 * @param mol Molecule to calculate fingerprint for. Must not be null.
		 * @param settings Fingerprint settings to apply. Must not be null.
		 * 
		 * @throws NullPointerException Thrown, if one of the settings is null.
		 */
		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
			return RDKFuncs.MACCSFingerprintMol(mol);
		}
	},

	pattern("Pattern") {
		@Override
		public FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
				final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
				final int iNumBits, final int iRadius, final int iLayerFlags,
				final int iAvalonQueryFlag, final int iAvalonBitFlags) {
			return new DefaultFingerprintSettings(toString(),
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					iNumBits,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE,
					FingerprintSettings.UNAVAILABLE);
		}

		@Override
		public void validateSpecification(final FingerprintSettings settings)
				throws InvalidFingerprintSettingsException {
			super.validateSpecification(settings);
			if (settings.getNumBits() <= 0) {
				throw new InvalidFingerprintSettingsException("Number of bits must be a positive number > 0.");
			}
		}

		@Override
		public ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings) {
				return RDKFuncs.PatternFingerprintMol(mol, settings.getNumBits());
		}
	};

	//
	// Constants
	//


	/**
	 * This lock prevents two calls at the same time into the Avalon Fingerprint functionality,
	 * which has caused crashes under Windows 7 before.
	 * Once there is a fix implemented in the RDKit (or somewhere else?) we can
	 * remove this lock again.
	 */
	public static final Object AVALON_FP_LOCK = new Object();

	//
	// Members
	//

	private final String m_strName;

	//
	// Constructors
	//

	/**
	 * Creates a new fingerprint type enumeration value.
	 * 
	 * @param strName Name to be shown as string representation.
	 */
	private FingerprintType(final String strName) {
		m_strName = strName;
	}

	/**
	 * Creates a new fingerprint settings object for a fingerprint type.
	 * Not all parameters are used for all fingerprints. This method
	 * takes are that only those parameters are included in the
	 * fingerprint specification, if they are are really used.
	 * 
	 * @param iTorsionPathLength Torsion Path Length value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iMinPath Min Path value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iMaxPath Min Path value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iAtomPairMinPath Min Path value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iAtomPairMaxPath Min Path value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iNumBits Num Bits (Length) value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iRadius Radius value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iLayerFlags Layer Flags value. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iAvalonQueryFlag Avalon query flag. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * @param iAvalonBitFlags Avalon bit flags. Can be -1 ({@link FingerprintSettings#UNAVAILABLE}.
	 * 
	 * @return Specification of the fingerprint based on the passed in
	 * 		values. Never null.
	 */
	public abstract FingerprintSettings getSpecification(final int iTorsionPathLength, final int iMinPath,
			final int iMaxPath, final int iAtomPairMinPath, final int iAtomPairMaxPath,
			final int iNumBits, final int iRadius, final int iLayerFlags,
			final int iAvalonQueryFlag, final int iAvalonBitFlags);

	/**
	 * Validates the passed in settings for a fingerprint type. This basis method checks two things:
	 * 1. That the setting object is not null, 2. If the fingerprint type can calculate rooted
	 * fingerprints and a rooted fingerprint is desired, it checks that the atom list reference
	 * is set.
	 * 
	 * @param settings Fingerprint settings to be validated.
	 * 
	 * @throws InvalidFingerprintSettingsException Thrown, if settings are invalid and cannot be used.
	 */
	public void validateSpecification(final FingerprintSettings settings) throws InvalidFingerprintSettingsException {
		if (settings == null) {
			throw new InvalidFingerprintSettingsException("No fingerprint settings available.");
		}
	}

	/**
	 * Calculates the fingerprint based on the specified settings. Important:
	 * It is the responsibility of the caller of the function to free memory
	 * for the returned fingerprint when it is not needed anymore. Call
	 * the {@link ExplicitBitVect#delete()} for this purpose.
	 * 
	 * @param settings Fingerprint settings. Must not be null.
	 * 
	 * @return Fingerprint or null.
	 */
	public abstract ExplicitBitVect calculate(final ROMol mol, final FingerprintSettings settings);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return m_strName;
	}

	/**
	 * Tries to determine the fingerprint type based on the passed in string. First it
	 * will try to determine it by assuming that the passed in string is the
	 * name of the fingerprint type ({@link #name()}. If this fails, it will compare the
	 * string representation trying to find a match there ({@link #toString()}.
	 * If none is found it will return null.
	 */
	public static FingerprintType parseString(String str) {
		FingerprintType type = null;

		if (str != null) {
			try {
				type = FingerprintType.valueOf(str);
			}
			catch (final IllegalArgumentException exc) {
				// Ignored here
			}

			if (type == null) {
				str = str.trim().toUpperCase();
				for (final FingerprintType typeExisting : FingerprintType.values()) {
					if (str.equals(typeExisting.toString().toUpperCase())) {
						type = typeExisting;
						break;
					}
				}
			}
		}

		return type;
	}

	/**
	 * Determines, if the two specified fingerprint setting objects are compatible.
	 * They are compatible if they are both not null and if the settings are the same
	 * except for the detail information for rooted fingerprints (atom list column).
	 * 
	 * @param fps1 Fingerprint settings 1. Can be null.
	 * @param fps2 Fingerprint settings 2. Can be null.
	 * 
	 * @return True, if both settings are compatible. False otherwise.
	 */
	public static boolean isCompatible(final FingerprintSettings fps1, final FingerprintSettings fps2) {
		boolean bRet = false;

		if (fps1 != null && fps2 != null) {
			bRet = CommonUtils.equals(fps1, fps2);
		}

		return bRet;
	}
}