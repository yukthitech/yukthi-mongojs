package com.yukthitech.mongojs.common;

import com.yukthitech.utils.cli.CliArgument;

/**
 * Command line arguments bean expected from command line.
 * 
 * @author akiran
 */
public class MongoJsArguments
{
	/**
	 * Db replicas to be used.
	 */
	@CliArgument(name = "r", longName = "replicas", description = "Db replicas", required = true)
	private String replicas;

	/**
	 * Default database to be used.
	 */
	@CliArgument(name = "db", longName = "database", description = "Default database to be used", required = false)
	private String dbname;

	/**
	 * Username to be used for authentication.
	 */
	@CliArgument(name = "un", longName = "username", description = "Username to be used for authentication", required = false)
	private String userName;

	/**
	 * Password to be used for authentication.
	 */
	@CliArgument(name = "pwd", longName = "password", description = "Password to be used for authentication", required = false)
	private String password;

	/**
	 * If true, then it will indicate the db connectivity should happen via SSL. Default: false.
	 */
	@CliArgument(name = "ssl", longName = "enable-ssl", 
			description = "If true, then it will indicate the db connectivity should happen via SSL. Default: false", required = false)
	private boolean enableSsl = false;

	/**
	 * Gets the default database to be used.
	 *
	 * @return the default database to be used
	 */
	public String getDbname()
	{
		return dbname;
	}

	/**
	 * Sets the default database to be used.
	 *
	 * @param dbname
	 *            the new default database to be used
	 */
	public void setDbname(String dbname)
	{
		this.dbname = dbname;
	}

	/**
	 * Gets the username to be used for authentication.
	 *
	 * @return the username to be used for authentication
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Sets the username to be used for authentication.
	 *
	 * @param userName
	 *            the new username to be used for authentication
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * Gets the password to be used for authentication.
	 *
	 * @return the password to be used for authentication
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Sets the password to be used for authentication.
	 *
	 * @param password
	 *            the new password to be used for authentication
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Gets the db replicas to be used.
	 *
	 * @return the db replicas to be used
	 */
	public String getReplicas()
	{
		return replicas;
	}

	/**
	 * Sets the db replicas to be used.
	 *
	 * @param replicas the new db replicas to be used
	 */
	public void setReplicas(String replicas)
	{
		this.replicas = replicas;
	}

	/**
	 * Checks if is if true, then it will indicate the db connectivity should
	 * happen via SSL. Default: false.
	 *
	 * @return the if true, then it will indicate the db connectivity should
	 *         happen via SSL
	 */
	public boolean isEnableSsl()
	{
		return enableSsl;
	}

	/**
	 * Sets the if true, then it will indicate the db connectivity should happen
	 * via SSL. Default: false.
	 *
	 * @param enableSsl
	 *            the new if true, then it will indicate the db connectivity
	 *            should happen via SSL
	 */
	public void setEnableSsl(boolean enableSsl)
	{
		this.enableSsl = enableSsl;
	}
}
