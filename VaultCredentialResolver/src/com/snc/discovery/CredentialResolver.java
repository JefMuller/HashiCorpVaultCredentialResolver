package com.snc.discovery;

import java.util.*;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import com.service_now.mid.services.Config;

/**
* Credential Resolver for Vault secrets management solution from HashiCorp.
* Use Vault Java Driver a community written zero-dependency Java client 
*
* @author  Jean-François (Jef) Muller
* @version 0.6
* @since   2020-05-10 
*/
public class CredentialResolver {

	// These are the permissible names of arguments passed INTO the resolve()
	// method.

	// the string identifier as configured on the ServiceNow instance...
	public static final String ARG_ID = "id";

	// a dotted-form string IPv4 address (like "10.22.231.12") of the target
	// system...
	public static final String ARG_IP = "ip";

	// the string type (ssh, snmp, etc.) of credential as configured on the
	// instance...
	public static final String ARG_TYPE = "type";

	// the string MID server making the request, as configured on the
	// instance...
	public static final String ARG_MID = "mid";

	// These are the permissible names of values returned FROM the resolve()
	// method.

	// the string user name for the credential, if needed...
	public static final String VAL_USER = "user";

	// the string password for the credential, if needed...
	public static final String VAL_PSWD = "pswd";

	// the string pass phrase for the credential if needed:
	public static final String VAL_PASSPHRASE = "passphrase";

	// the string private key for the credential, if needed...
	public static final String VAL_PKEY = "pkey";

	private String address;
	private String token;
	
	public CredentialResolver() {
	}

	private void loadProps() {
		// Load Vault MID Properties
		address = Config.get().getProperty("mid.external_credentials.vault.address");
    	if(isNullOrEmpty(address))
        	throw new RuntimeException("[Vault] INFO - CredentialResolver mid.external_credentials.vault.address not set!");
    	token = Config.get().getProperty("mid.external_credentials.vault.token");
    	if(isNullOrEmpty(token))
        	throw new RuntimeException("[Vault] INFO - CredentialResolver mid.external_credentials.vault.token not set!");
	}

	/**
	 * Resolve a credential.
	 */
	public Map resolve(Map args) {
		loadProps();
		String id = (String) args.get(ARG_ID);
		String type = (String) args.get(ARG_TYPE);
		
		String username = "";
		String password = "";
		String passphrase = "";
		String private_key = "";
		
		if(id.equalsIgnoreCase("misbehave"))
			throw new RuntimeException("I've been a baaaaaaaaad CredentialResolver!");

		// Connect to vault and retrieve credential
		try {
			final VaultConfig config = new VaultConfig()
			        .address(address)
			        .token(token)
			        .build();
			final Vault vault = new Vault(config);
			switch(type) {
				// for below listed credential type , just retrieve user name and password 
				case "windows":
				case "ssh_password": // Type SSH
				case "vmware":
				case "jdbc":
				case "jms": 
				case "basic":
					// Read operation 
					// adjusted key to AD secret engine API
					username = vault.logical().read(id).getData().get("username");
					if(isNullOrEmpty(username)) {
						System.err.println("[Vault] ERROR - username not set!");
						break;
					}
					password = vault.logical().read(id).getData().get("current_password");
					if(isNullOrEmpty(password)) {
						System.err.println("[Vault] ERROR - password not set!");
						break;
					}
					break;
				// for below listed credential type , retrieve user name, password, ssh_passphrase, ssh_private_key
				case "ssh_private_key": 
				case "sn_cfg_ansible": 
				case "sn_disco_certmgmt_certificate_ca":
				case "cfg_chef_credentials":
				case "infoblox": 
				case "api_key":
					// Read operation
					username = vault.logical().read(id).getData().get("username");
					if(isNullOrEmpty(username)) {
						System.err.println("[Vault] ERROR - user_name not set!");
						break;
					}
					password = vault.logical().read(id).getData().get("current_password");
					if(isNullOrEmpty(password)) {
						System.err.println("[Vault] ERROR - password not set!");
						break;
					}
					passphrase = vault.logical().read(id).getData().get("ssh_passphrase");
					private_key = vault.logical().read(id).getData().get("ssh_private_key");
					break;
				case "ibm": ; // softlayer_user, softlayer_key, bluemix_key
				case "aws": ; // access_key, secret_key
				
				case "azure": ; // tenant_id, client_id, auth_method, secret_key
				case "gcp": ; // email , secret_key
				default:
					System.err.println("[Vault] INFO - CredentialResolver, not implemented credential type!");
					break;
			}
			
		} 
		catch (VaultException e) {
			// Catch block
			System.err.println("### Unable to connect to Vault: " + address + " #### ");
			e.printStackTrace();
		}
		// the resolved credential is returned in a HashMap...
		Map result = new HashMap();
		result.put(VAL_USER, username);
		result.put(VAL_PSWD, password);
		result.put(VAL_PKEY, private_key);
		result.put(VAL_PASSPHRASE, passphrase);
		return result;
	}

	public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
	/**
	 * Return the API version supported by this class.
	 */
	public String getVersion() {
		return "0.5";
	}

	public static void main(String[] args) {
		CredentialResolver obj = new CredentialResolver();
		obj.loadProps();
		}
}
