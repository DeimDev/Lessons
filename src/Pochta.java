import java.util.logging.Level;
import java.util.logging.Logger;

public class Pochta {

    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";

    /*
Интерфейс: сущность, которую можно отправить по почте.
У такой сущности можно получить от кого и кому направляется письмо.
*/
    public static interface Sendable {
        String getFrom();

        String getTo();
    }

    /*
Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
*/
    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    /*
Абстрактный класс,который позволяет абстрагировать логику хранения
источника и получателя письма в соответствующих полях класса.
*/
    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }

    /*
Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
*/
    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    /*
Посылка, содержимое которой можно получить с помощью метода `getContent`
*/
    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    /*
Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
*/
    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static class UntrustworthyMailWorker implements MailService {

        MailService[] ms;
        RealMailService rms;
        public UntrustworthyMailWorker(MailService[] ms) {
            this.ms = ms;
        }

        public Sendable processMail(Sendable mail) {
            Sendable m = mail;
            for (MailService s : ms) {
                m = s.processMail(m);
            }

            return m;
        }

        public RealMailService getRealMailService() {
            return this.rms;
        }

    }

    public static class Thief implements MailService {

        private int minPrice;
        private int countPrice = 0;

        public Thief(int minPrice) {
            this.minPrice = minPrice;
        }

        public int getStolenValue() {
            return countPrice;
        }

        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage && ((MailPackage) mail).getContent().getPrice() > minPrice) {
                countPrice += ((MailPackage) mail).getContent().getPrice();
                return new MailPackage(mail.getFrom(), mail.getTo(), new Package("stones instead of {content}", 0));
            }
            return mail;
        }
    }

    public static class Spy implements MailService {

        Logger LOGGER;

        public Spy(Logger log) {
            LOGGER = log;
        }

        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage) {
                String s;
                if (mail.getFrom().equals(AUSTIN_POWERS) || mail.getTo().equals(AUSTIN_POWERS)) {
                    s = String.format("Detected target mail correspondence: from %s to %s \"%s\"", mail.getFrom(), mail.getTo(), ((MailMessage) mail).getMessage());
                    LOGGER.log(Level.WARNING, s);
                    return mail;
                }
                s = String.format("Usual correspondence: from %s to %s", mail.getFrom(), mail.getTo());
                LOGGER.log(Level.INFO, s);
            }
            return mail;
        }
    }

    public static class Inspector implements MailService {

        public Inspector() {

        }

        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                if (((MailPackage) mail).getContent().getContent().equals(WEAPONS) || ((MailPackage) mail).getContent().getContent().equals(BANNED_SUBSTANCE)) {
                    throw new IllegalPackageException();
                } else if (((MailPackage) mail).getContent().getContent().startsWith("stones")) {
                    throw new StolenPackageException();
                }
            }
            return mail;
        }

    }

    static class IllegalPackageException extends RuntimeException {
        public IllegalPackageException() {
            super();
        }
    }

    static class StolenPackageException extends RuntimeException {
        public StolenPackageException() {
            super();
        }

    }


}
