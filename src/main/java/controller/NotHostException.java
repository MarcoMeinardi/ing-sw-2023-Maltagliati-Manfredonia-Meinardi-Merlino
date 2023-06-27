package controller;

/**
 * Exception thrown when a player, that is not the lobby's host. tries to make an action that requires the host's privileges.
 */
public class NotHostException extends Exception{}
